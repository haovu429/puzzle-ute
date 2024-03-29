package hcmute.puzzle.controller;

import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.dtos.news.RegisterUserDto;
import hcmute.puzzle.infrastructure.dtos.olds.*;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostFilterRequest;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.JobPost;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.BlogPostMapper;
import hcmute.puzzle.infrastructure.mappers.CandidateMapper;
import hcmute.puzzle.infrastructure.mappers.JobPostMapper;
import hcmute.puzzle.infrastructure.models.*;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.services.impl.*;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/common")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class CommonController {

	@Autowired
	CandidateService candidateService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmployerService employerService;

	@Autowired
	JobPostService jobPostService;

	@Autowired
	JobPostRepository jobPostRepository;

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	ApplicationRepository applicationRepository;

	@Autowired
	UserService userService;

	//  @Autowired
	//  Converter converter;

	@Autowired
	ExtraInfoService extraInfoService;

	@Autowired
	CompanyService companyService;

	@Autowired
	SearchService searchService;

	@Autowired
	ApplicationService applicationService;

	@Autowired
	ExperienceService experienceService;

	@Autowired
	BlogPostService blogPostService;

	@Autowired
	CommentService commentService;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	SecurityService securityService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	JobPostMapper jobPostMapper;

	@Autowired
	CandidateMapper candidateMapper;

	@Autowired
	BlogPostMapper blogPostMapper;

	@Autowired
	CurrentUserService currentUserService;

	@GetMapping("/job-post/get-all")
	DataResponse<Page<JobPostDto>> getAllJobPost(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size, @RequestParam(required = false) Long categoryId) {
		Pageable pageable = this.getPageable(page, size);
		List<Long> categoryIds = new ArrayList<>();
		if (categoryId != null) {
			categoryIds.add(categoryId);
		}
		JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder()
																		.categoryIds(categoryIds)
																		.isActive(true)
																		.build();
		Page<JobPostDto> jobPostDtos = jobPostService.filterJobPost(jobPostFilterRequest, pageable);
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/job-post/get-one/{jobPostId}")
	DataResponse<JobPostDto> getJobPostById(@PathVariable(value = "jobPostId") long jobPostId) {
		//jobPostService.countJobPostView(jobPostId);
		try {
			User currenUser = currentUserService.getCurrentUserOptional().orElse(null);
			if (currenUser != null) {
				jobPostService.viewJobPost(currenUser.getEmail(), jobPostId);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		JobPostDto jobPostDto = jobPostService.getOne(jobPostId);
		return new DataResponse<>(jobPostDto);
	}

	@GetMapping("/company")
	public DataResponse<Page<CompanyResponse>> getAllCompany(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = this.getPageable(page, size);
		Page<CompanyResponse> companyResponses = companyService.getAll(pageable);
		return new DataResponse<>(companyResponses);
	}

	@GetMapping("/company/get-one-company/{id}")
	public DataResponse<CompanyResponse> getOneCompany(@PathVariable Long id) {
		CompanyResponse companyResponse = companyService.getOneById(id);
		return new DataResponse<>(companyResponse);
	}

	@GetMapping("/get-all-extra-info-by-type")
	public DataResponse<List<ExtraInfoDto>> getAllExtraInfoByType(@RequestParam String type) {
		List<ExtraInfoDto> extraInfoDtos = extraInfoService.getByType(type);
		return new DataResponse<>(extraInfoDtos);
	}

	@GetMapping("/job-post/personal-view")
	public DataResponse<List<JobPostDto>> getAllExtraInfoByType(@RequestParam(required = false) Integer size) {
		List<JobPostDto> jobPostDtoList = new ArrayList<>();
		//		List<JobPostDto> jobPostDtoSuggestFromJobAlert = new ArrayList<>();
		List<JobPostDto> recentViewJobPost;
		List<JobPostDto> normalJobPost = new ArrayList<>();
		try {
			normalJobPost = new ArrayList<>(jobPostService.filterJobPost(Pageable.unpaged()).getContent());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		User user = currentUserService.getCurrentUserOptional().orElse(null);
		if (user != null) {
			//			jobPostDtoSuggestFromJobAlert = jobPostService.filterJobPostByAllJobAlert(user.getId());
			//			normalJobPost.removeAll(jobPostDtoSuggestFromJobAlert);
			//			jobPostDtoList.addAll(jobPostDtoSuggestFromJobAlert);
			recentViewJobPost = jobPostService.getRecentViewJobPost(user.getId());
			List<Long> recentViewJobPostId = recentViewJobPost.stream().map(JobPostDto::getId).toList();
			normalJobPost.removeIf(jobPostDto -> recentViewJobPostId.contains(jobPostDto.getId()));
			jobPostDtoList.addAll(recentViewJobPost);
		}
		jobPostDtoList.addAll(normalJobPost);
		if (size != null) {
			jobPostDtoList = jobPostDtoList.subList(0, size);
		}

		jobPostService.processListJobPost(jobPostDtoList);
		return new DataResponse<>(jobPostDtoList);
	}

	@PostMapping("/job-post-filter")
	public DataResponse<List<JobPostDto>> filterJobPostV2(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size, @RequestBody(required = false) JobPostFilter jobPostFilter) {
		try {
			// Apply new filter from old filter
			Integer experienceYear = null;
			if (jobPostFilter.getExperienceYear() != null && !jobPostFilter.getExperienceYear().isEmpty()) {
				try {
					if (Integer.valueOf(jobPostFilter.getExperienceYear().get(0)) != -1) {
						experienceYear = Integer.valueOf(jobPostFilter.getExperienceYear().get(0));
					}
				} catch (NumberFormatException e) {
					log.error(e.getMessage(), e);
				}
			}
			List<String> searchKeys = new ArrayList<>();
			if (jobPostFilter.getTitles() != null && !jobPostFilter.getTitles().isEmpty()) {
				searchKeys.addAll(jobPostFilter.getTitles());
			}
			if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
				searchKeys.addAll(jobPostFilter.getSkills());
			}
			if (jobPostFilter.getOthers() != null && !jobPostFilter.getOthers().isEmpty()) {
				searchKeys.addAll(jobPostFilter.getOthers());
			}

			String city = null;
			if (jobPostFilter.getCities() != null && !jobPostFilter.getCities().isEmpty()) {
				city = jobPostFilter.getCities().get(0);
			}

			String position = null;
			if (jobPostFilter.getPositions() != null && !jobPostFilter.getPositions().isEmpty()) {
				position = jobPostFilter.getPositions().get(0);
			}

			JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder()
																			.minBudget(jobPostFilter.getMinBudget())
																			.maxBudget(jobPostFilter.getMaxBudget())
																			.experienceYear(experienceYear)
																			.searchKeys(searchKeys)
																			.employmentTypes(
																					jobPostFilter.getEmploymentTypes())
																			.city(city)
																			.position(position)
																			.skills(jobPostFilter.getSkills())
																			.categoryIds(jobPostFilter.getCategoryIds())
																			.companyIds(jobPostFilter.getCompanyIds())
																			.isActive(true)
																			.numDayAgo(jobPostFilter.getNumDayAgo())
																			.sortColumn("updatedAt")
																			.build();
			Pageable pageable = this.getPageable(page, size);
			Page<JobPostDto> jobPostDtos = jobPostService.filterJobPost(jobPostFilterRequest, pageable);
			List<JobPostDto> jobPostDtoList = jobPostDtos.getContent();
			jobPostService.processListJobPost(jobPostDtoList);
			return new DataResponse<>(jobPostDtoList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping("/job-post-filter-old")
	public DataResponse<List<JobPostDto>> filterJobPost(@RequestBody(required = false) JobPostFilter jobPostFilter) {
		Map<String, List<ModelQuery>> fieldSearchValue = new HashMap<>();
		Map<String, List<ModelQuery>> fieldSearchValueSpecial = new HashMap<>();
		List<SearchBetween> searchBetweenList = new ArrayList<>();

		//    if (jobPostFilter.getNumDayAgo() != -1 && jobPostFilter.getNumDayAgo() > 0) {
		//      TimeUtil timeUtil = new TimeUtil();
		//      Date timeLine = timeUtil.upDownTime_TimeUtil(new Date(), jobPostFilter.getNumDayAgo(),
		// 0, 0);
		//      SearchBetween searchForCreateTime =
		//          new SearchBetween(
		//              "createTime", new ModelQuery(
		//                  ModelQuery.TYPE_QUERY_GREATER,
		//                  ModelQuery.TYPE_ATTRIBUTE_DATE,TimeUtil.dateToString(timeLine,
		// TimeUtil.FORMAT_TIME)), null);
		//      searchBetweenList.add(searchForCreateTime);
		//    }

		if (jobPostFilter.getNumDayAgo() != -1 && jobPostFilter.getNumDayAgo() > 0) {
			TimeUtil timeUtil = new TimeUtil();
			Date timeLine = timeUtil.upDownTime_TimeUtil(new Date(), jobPostFilter.getNumDayAgo(), 0, 0);
			List<ModelQuery> idJobCreateTimeGreater = jobPostRepository.getJobPostIdByActiveAndLessThenCreatedTime(true,
																												   timeLine)
																	   .stream()
																	   .map(id -> new ModelQuery(
																			   ModelQuery.TYPE_QUERY_IN,
																			   ModelQuery.TYPE_ATTRIBUTE_NUMBER, id))
																	   .collect(Collectors.toList());
			System.out.println(idJobCreateTimeGreater.size());
			fieldSearchValueSpecial.put("id", idJobCreateTimeGreater);
		}

		if (jobPostFilter.getMinBudget() != null) {
			SearchBetween searchForBudgetMin = new SearchBetween("minBudget",
																 new ModelQuery(ModelQuery.TYPE_QUERY_GREATER,
																				ModelQuery.TYPE_ATTRIBUTE_NUMBER,
																				Double.valueOf(
																						jobPostFilter.getMinBudget())),
																 null);
			searchBetweenList.add(searchForBudgetMin);
		}

		if (jobPostFilter.getMaxBudget() != null) {
			SearchBetween searchForBudgetMax = new SearchBetween("maxBudget", null,
																 new ModelQuery(ModelQuery.TYPE_QUERY_LESS,
																				ModelQuery.TYPE_ATTRIBUTE_NUMBER,
																				Double.valueOf(
																						jobPostFilter.getMaxBudget())));
			searchBetweenList.add(searchForBudgetMax);
		}

		if (jobPostFilter.getExperienceYear() != null && !jobPostFilter.getExperienceYear().isEmpty() && Double.valueOf(
				jobPostFilter.getExperienceYear().get(0)) > 0) {
			SearchBetween searchForExperienceYearMin = new SearchBetween("experienceYear", null,
																		 new ModelQuery(ModelQuery.TYPE_QUERY_LESS,
																						ModelQuery.TYPE_ATTRIBUTE_NUMBER,
																						Double.valueOf(
																								jobPostFilter.getExperienceYear()
																											 .get(0))));
			searchBetweenList.add(searchForExperienceYearMin);
		}

		if (jobPostFilter.getTitles() != null && !jobPostFilter.getTitles().isEmpty()) {
			fieldSearchValue.put("title", jobPostFilter.getTitles()
													   .stream()
													   .map(title -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																					ModelQuery.TYPE_ATTRIBUTE_STRING,
																					title))
													   .collect(Collectors.toList()));
		}

		//    if (jobPostFilter.getExperienceYear() != null &&
		// !jobPostFilter.getExperienceYear().isEmpty()) {
		//      List<Long> expNums =
		//          jobPostFilter.getExperienceYear().stream()
		//              .map(exp -> Long.valueOf(exp))
		//              .collect(Collectors.toList());
		//      fieldSearchValue.put(
		//          "experienceYear",
		//          expNums.stream()
		//              .map(
		//                  num ->
		//                      new ModelQuery(
		//                          ModelQuery.TYPE_QUERY_EQUAL,
		//                          ModelQuery.TYPE_ATTRIBUTE_NUMBER,
		//                          Long.valueOf(num)))
		//              .collect(Collectors.toList()));
		//    }

		if (jobPostFilter.getEmploymentTypes() != null && !jobPostFilter.getEmploymentTypes().isEmpty()) {
			fieldSearchValue.put("employmentType", jobPostFilter.getEmploymentTypes()
																.stream()
																.map(employmentType -> new ModelQuery(
																		ModelQuery.TYPE_QUERY_LIKE,
																		ModelQuery.TYPE_ATTRIBUTE_STRING,
																		employmentType))
																.collect(Collectors.toList()));
		}

		if (jobPostFilter.getCities() != null && !jobPostFilter.getCities().isEmpty()) {
			fieldSearchValue.put("city", jobPostFilter.getCities()
													  .stream()
													  .map(city -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																				  ModelQuery.TYPE_ATTRIBUTE_STRING,
																				  city))
													  .collect(Collectors.toList()));
		}

		if (jobPostFilter.getPositions() != null && !jobPostFilter.getPositions().isEmpty()) {
			fieldSearchValue.put("positions", jobPostFilter.getPositions()
														   .stream()
														   .map(position -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																						   ModelQuery.TYPE_ATTRIBUTE_STRING,
																						   position))
														   .collect(Collectors.toList()));
		}

		if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
			fieldSearchValue.put("skills", jobPostFilter.getSkills()
														.stream()
														.map(skill -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																					 ModelQuery.TYPE_ATTRIBUTE_STRING,
																					 skill))
														.collect(Collectors.toList()));
		}

		// filer job post active
		List<Boolean> booleanList = new ArrayList<>();
		booleanList.add(Boolean.TRUE);

		fieldSearchValue.put("isActive", booleanList.stream()
													.map(boo -> new ModelQuery(ModelQuery.TYPE_QUERY_EQUAL,
																			   ModelQuery.TYPE_ATTRIBUTE_BOOLEAN, boo))
													.collect(Collectors.toList()));

		if (jobPostFilter.getCategoryIds() != null && !jobPostFilter.getCategoryIds().isEmpty()) {
			fieldSearchValue.put("category", jobPostFilter.getCategoryIds()
														  .stream()
														  .map(id -> new ModelQuery(ModelQuery.TYPE_QUERY_EQUAL,
																					ModelQuery.TYPE_ATTRIBUTE_NUMBER,
																					id))
														  .collect(Collectors.toList()));
		}

		List<String> commonFieldSearch = new ArrayList<>();
		List<ModelQuery> valueCommonFieldSearch = null;
		if (jobPostFilter.getOthers() != null && !jobPostFilter.getOthers().isEmpty()) {
			valueCommonFieldSearch = jobPostFilter.getOthers()
												  .stream()
												  .map(other -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																			   ModelQuery.TYPE_ATTRIBUTE_STRING, other))
												  .collect(Collectors.toList());
			commonFieldSearch.add("description");
			commonFieldSearch.add("name");
		}
		List<JobPost> jobPostEntities = searchService.filterObject("JobPost", searchBetweenList, fieldSearchValue,
																   fieldSearchValueSpecial, commonFieldSearch,
																   valueCommonFieldSearch,
																   jobPostFilter.getNoOfRecords(),
																   jobPostFilter.getPageIndex(),
																   jobPostFilter.isSortById());

		List<JobPostDto> jobPostDtos = jobPostEntities.stream().map(jobPost -> {
			JobPostDto jobPostDTO = jobPostMapper.jobPostToJobPostDto(jobPost);
			jobPostDTO.setDescription(null);
			return jobPostDTO;
		}).collect(Collectors.toList());

		// JobPostFilter jobPostFilter1 = new JobPostFilter();

		return new DataResponse<>(jobPostDtos);
	}

	@PostMapping("/candidate-filter")
	public DataResponse<List<CandidateDto>> filterCandidate(@RequestBody CandidateFilter candidateFilter) {

		Map<String, List<ModelQuery>> fieldSearchValue = new HashMap<>();
		if (candidateFilter.getEducationLevels() != null && !candidateFilter.getEducationLevels().isEmpty()) {
			fieldSearchValue.put("educationLevel", candidateFilter.getEducationLevels()
																  .stream()
																  .map(educationLevel -> new ModelQuery(
																		  ModelQuery.TYPE_QUERY_LIKE,
																		  ModelQuery.TYPE_ATTRIBUTE_STRING,
																		  educationLevel))
																  .collect(Collectors.toList()));
		}

		if (candidateFilter.getSkills() != null && !candidateFilter.getSkills().isEmpty()) {
			fieldSearchValue.put("skills", candidateFilter.getSkills()
														  .stream()
														  .map(skill -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																					   ModelQuery.TYPE_ATTRIBUTE_STRING,
																					   skill))
														  .collect(Collectors.toList()));
		}

		if (candidateFilter.getPositions() != null && !candidateFilter.getPositions().isEmpty()) {
			fieldSearchValue.put("positions", candidateFilter.getPositions()
															 .stream()
															 .map(position -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																							 ModelQuery.TYPE_ATTRIBUTE_STRING,
																							 position))
															 .collect(Collectors.toList()));
		}

		if (candidateFilter.getServices() != null && !candidateFilter.getServices().isEmpty()) {
			fieldSearchValue.put("services", candidateFilter.getServices()
															.stream()
															.map(service -> new ModelQuery(ModelQuery.TYPE_QUERY_LIKE,
																						   ModelQuery.TYPE_ATTRIBUTE_STRING,
																						   service))
															.collect(Collectors.toList()));
		}
		List<String> commonFieldSearch = new ArrayList<>();
		List<ModelQuery> valueCommonFieldSearch = candidateFilter.getOthers()
																 .stream()
																 .map(other -> new ModelQuery(
																		 ModelQuery.TYPE_QUERY_LIKE,
																		 ModelQuery.TYPE_ATTRIBUTE_STRING, other))
																 .collect(Collectors.toList());

		if (candidateFilter.getOthers() != null && !candidateFilter.getOthers().isEmpty()) {
			commonFieldSearch.add("introduction");
			commonFieldSearch.add("phoneNum");
		}
		List<Candidate> candidates = searchService.filterObject("CandidateEntity", null, fieldSearchValue, null,
																commonFieldSearch, valueCommonFieldSearch,
																candidateFilter.getNoOfRecords(),
																candidateFilter.getPageIndex(),
																candidateFilter.isSortById());
		List<CandidateDto> candidateDTOS = candidates.stream()
													 .map(candidate -> candidateMapper.candidateToCandidateDto(
															 candidate))
													 .collect(Collectors.toList());
		// CandidateFilter candidateFilter1 = new CandidateFilter();
		return new DataResponse<>(candidateDTOS);
	}

	@GetMapping("/get-all-extra-info")
	public DataResponse<Page<ExtraInfoDto>> getAllExtraInfo(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = this.getPageable(page, size);
		Page<ExtraInfoDto> extraInfoDtos = extraInfoService.getAll(pageable);
		return new DataResponse<>(extraInfoDtos);
	}

	@GetMapping("/employer/get-employer-by-id/{id}")
	DataResponse<EmployerDto> getEmployerById(@PathVariable long id) {
		EmployerDto employerDto = employerService.getOne(id);
		return new DataResponse<>(employerDto);
	}

	@PostMapping("/register")
	public DataResponse<String> registerAccount(@RequestBody RegisterUserDto user) {
			User userEntity = userService.registerUser(user).orElse(null);
			if (userEntity != null) {
				//securityService.sendTokenVerifyAccount(userEntity.getEmail());
				return new DataResponse<>("Create user " + user.getEmail() + " success");
			}
		return new DataResponse<>("Error while sent mail verify");
	}

	@GetMapping("/get-hot-job-post")
	public DataResponse<List<JobPostDto>> getHotJobPost() {
		List<JobPostDto> jobPostDtos = jobPostService.getHotJobPost();
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/get-job-post-due-soon")
	public DataResponse<List<JobPostDto>> getJobPostDueSoon() {
		//List<JobPostDto> jobPostDtos = jobPostService.getJobPostDueSoon();
		JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder()
																		.sortColumn("deadline")
																		.isAscSort(true)
																		.build();
		List<JobPostDto> jobPostDtos = jobPostService.filterJobPost(jobPostFilterRequest, Pageable.unpaged())
													 .getContent();
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/get-profile-candidate/{candidateId}")
	DataResponse<CandidateDto> getProfileCandidate(@PathVariable long candidateId) {
		CandidateDto candidateDto = candidateService.getOne(candidateId);
		return new DataResponse<>(candidateDto);
	}

	@GetMapping("/get-active-job-post")
	public DataResponse<List<JobPostDto>> getActiveJobPost() {
		JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder().sortColumn("updatedAt").build();
		List<JobPostDto> jobPostDtos = jobPostService.filterJobPost(jobPostFilterRequest, Pageable.unpaged())
													 .getContent();
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/get-experience-by-candidate-id/{id}")
	DataResponse<List<ExperienceDto>> getAllExperienceByCandidateId(@PathVariable(value = "id") long id) {
		List<ExperienceDto> experienceDtos = experienceService.getAllExperienceByCandidateId(id);
		return new DataResponse<>(experienceDtos);
	}

	@GetMapping("/candidate-profile/{candidateId}")
	DataResponse<CandidateDto> getCandidateProfile(@PathVariable(value = "candidateId") long candidateId) {
		CandidateDto candidateDto = candidateService.getOne(candidateId);
		return new DataResponse<>(candidateDto);
	}

	@GetMapping("/get-amount-application-to-job-post/{jobPostId}")
	DataResponse<Long> getAmountApplicationToEmployer(@PathVariable(value = "jobPostId") long jobPostId) {
		long amount = applicationService.getAmountApplicationByJobPostId(jobPostId);
		return new DataResponse<>(amount);
	}

	@GetMapping("/get-job-post-amount")
	public DataResponse<Long> getJobPostAmount() {
		long amount = jobPostService.getJobPostAmount();
		return new DataResponse<>(amount);
	}

	@GetMapping("/view-job-post/{jobPostId}")
	public DataResponse<Long> viewJobPost(@PathVariable(value = "jobPostId") long jobPostId) {
		long count = jobPostService.countJobPostViewReturnDataResponse(jobPostId);
		return new DataResponse<>(count);
	}

	@GetMapping("/get-application-amount")
	public DataResponse<Long> getApplicationAmount() {
		return new DataResponse<>(applicationService.getApplicationAmount());
	}

	@GetMapping("/view-blog-post/{blogPostId}")
	public DataResponse viewBlogPost(@PathVariable long blogPostId) {
		return blogPostService.getOneById(blogPostId);
	}

	//  @PostMapping("/comment/{blogPostId}")
	//  public DataResponse createComment(
	//      @RequestBody CreateCommentRequest createCommentRequest, @PathVariable long blogPostId) {
	//    CommentDto commentDto = commentService.addComment(createCommentRequest, blogPostId);
	//    return new DataResponse(commentDto);
	//  }

	@GetMapping("/like-comment/{commentId}")
	public DataResponse<String> likeComment(@PathVariable long commentId) {
		commentService.likeComment(commentId);
		return new DataResponse<>("Success");
	}

	@GetMapping("/dis-like-comment/{commentId}")
	public DataResponse<String> disLikeComment(@PathVariable long commentId) {
		commentService.disLikeComment(commentId);
		return new DataResponse<>("Success");
	}

	@GetMapping("/blog-post")
	public DataResponse<Page<BlogPostDto>> getAllBlogPost(@RequestParam(required = false) Long blogCategoryId,
			@RequestParam(required = false) String searchKey, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = Pageable.unpaged();
		if (page != null && size != null) {
			pageable = PageRequest.of(page, size);
		}
		BlogPostFilterRequest blogPostFilterRequest = BlogPostFilterRequest.builder()
																		   .categoryId(blogCategoryId)
																		   .searchKey(searchKey)
																		   .isActive(true)
																		   .isPublic(true)
																		   .isAscSort(false)
																		   .sortColumn("createdAt")
																		   .build();
		Page<BlogPostDto> blogPostDtos = blogPostService.filterBlogPost(blogPostFilterRequest, pageable)
														.map(blogPostMapper::blogPostToBlogPostDto);
		return new DataResponse<>(blogPostDtos);
	}

	@GetMapping("/blog-post/blog-category-with-post-amount")
	public DataResponse<List<BlogCategoryResponseWithBlogPostAmount>> getBlogCateWithPostAmount() {
		List<BlogCategoryResponseWithBlogPostAmount> blogCateWithPostAmounts = blogPostService.getBlogCategoryWithBlogPostAmount();
		return new DataResponse<>(blogCateWithPostAmounts);
	}

	@GetMapping("/get-all-category")
	public DataResponse getAllCategory() {
		return categoryService.getAll();
	}

	private Pageable getPageable(Integer page, Integer size) {
		Pageable pageable = Pageable.unpaged();
		if (page != null && size != null) {
			pageable = Pageable.ofSize(size).withPage(page);
		}
		return pageable;
	}
	@GetMapping("/comment/get-by-blog-post")
	public DataResponse<List<CommentDto>> getCommentListByJobPostId(@RequestParam long blogPostId) {
		List<CommentDto> commentDtos = commentService.getAllByBlogPostId(blogPostId);
		return new DataResponse<>(commentDtos);
	}


}
