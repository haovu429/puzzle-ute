package hcmute.puzzle.controller;

import freemarker.template.TemplateException;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.news.RegisterUserDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.olds.JobPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.CandidateEntity;
import hcmute.puzzle.infrastructure.entities.JobPostEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.models.CandidateFilter;
import hcmute.puzzle.infrastructure.models.JobPostFilter;
import hcmute.puzzle.infrastructure.models.ModelQuery;
import hcmute.puzzle.infrastructure.models.SearchBetween;
import hcmute.puzzle.infrastructure.models.payload.request.comment.CreateCommentPayload;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/common")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class CommonController {

  @Autowired CandidateService candidateService;

  @Autowired UserRepository userRepository;

  @Autowired EmployerService employerService;

  @Autowired JobPostService jobPostService;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired ApplicationRepository applicationRepository;

  @Autowired UserService userService;

  @Autowired Converter converter;

  @Autowired ExtraInfoService extraInfoService;

  @Autowired CompanyService companyService;

  @Autowired SearchService searchService;

  @Autowired ApplicationService applicationService;

  @Autowired ExperienceService experienceService;

  @Autowired BlogPostService blogPostService;

  @Autowired CommentService commentService;

  @Autowired ModelMapper modelMapper;

  @Autowired SecurityService securityService;

  @GetMapping("/job-post/get-all")
  ResponseObject getAllJobPost() {
    return jobPostService.getAll();
  }

  @GetMapping("/job-post/get-one/{jobPostId}")
  ResponseObject getJobPostById(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable(value = "jobPostId") long jobPostId) {
    jobPostService.countJobPostView(jobPostId);
    try {
      if (token != null && !token.isEmpty() && !token.isBlank()) {
        Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
        jobPostService.viewJobPost(linkUser.get().getId(), jobPostId);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return jobPostService.getOne(jobPostId);
  }

  @GetMapping("/company")
  public ResponseObject getAllCompany() {
    return companyService.getAll();
  }

  @GetMapping("/company/get-one-company/{id}")
  public ResponseObject getOneCompany(@PathVariable Long id) {
    return companyService.getOneById(id);
  }

  @GetMapping("/get-all-extra-info-by-type")
  public ResponseObject getAllExtraInfoByType(@RequestParam String type) {
    return extraInfoService.getByType(type);
  }

  @PostMapping("/job-post-filter")
  public ResponseObject filterJobPost(@RequestBody(required = false) JobPostFilter jobPostFilter) {
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
      List<ModelQuery> idJobCreateTimeGreater =
          jobPostRepository.getJobPostIdByActiveAndLessThenCreatedTime(true, timeLine).stream()
              .map(
                  id ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_IN, ModelQuery.TYPE_ATTRIBUTE_NUMBER, id))
              .collect(Collectors.toList());
      System.out.println(idJobCreateTimeGreater.size());
      fieldSearchValueSpecial.put("id", idJobCreateTimeGreater);
    }

    if (jobPostFilter.getMinBudget() != null) {
      SearchBetween searchForBudgetMin =
          new SearchBetween(
              "minBudget",
              new ModelQuery(
                  ModelQuery.TYPE_QUERY_GREATER,
                  ModelQuery.TYPE_ATTRIBUTE_NUMBER,
                  Double.valueOf(jobPostFilter.getMinBudget())),
              null);
      searchBetweenList.add(searchForBudgetMin);
    }

    if (jobPostFilter.getMaxBudget() != null) {
      SearchBetween searchForBudgetMax =
          new SearchBetween(
              "maxBudget",
              null,
              new ModelQuery(
                  ModelQuery.TYPE_QUERY_LESS,
                  ModelQuery.TYPE_ATTRIBUTE_NUMBER,
                  Double.valueOf(jobPostFilter.getMaxBudget())));
      searchBetweenList.add(searchForBudgetMax);
    }

    if (jobPostFilter.getExperienceYear() != null
        && !jobPostFilter.getExperienceYear().isEmpty()
        && Double.valueOf(jobPostFilter.getExperienceYear().get(0)) > 0) {
      SearchBetween searchForExperienceYearMin =
          new SearchBetween(
              "experienceYear",
              null,
              new ModelQuery(
                  ModelQuery.TYPE_QUERY_LESS,
                  ModelQuery.TYPE_ATTRIBUTE_NUMBER,
                  Double.valueOf(jobPostFilter.getExperienceYear().get(0))));
      searchBetweenList.add(searchForExperienceYearMin);
    }

    if (jobPostFilter.getTitles() != null && !jobPostFilter.getTitles().isEmpty()) {
      fieldSearchValue.put(
          "title",
          jobPostFilter.getTitles().stream()
              .map(
                  title ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, title))
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

    if (jobPostFilter.getEmploymentTypes() != null
        && !jobPostFilter.getEmploymentTypes().isEmpty()) {
      fieldSearchValue.put(
          "employmentType",
          jobPostFilter.getEmploymentTypes().stream()
              .map(
                  employmentType ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE,
                          ModelQuery.TYPE_ATTRIBUTE_STRING,
                          employmentType))
              .collect(Collectors.toList()));
    }

    if (jobPostFilter.getCities() != null && !jobPostFilter.getCities().isEmpty()) {
      fieldSearchValue.put(
          "city",
          jobPostFilter.getCities().stream()
              .map(
                  city ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, city))
              .collect(Collectors.toList()));
    }

    if (jobPostFilter.getPositions() != null && !jobPostFilter.getPositions().isEmpty()) {
      fieldSearchValue.put(
          "positions",
          jobPostFilter.getPositions().stream()
              .map(
                  position ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, position))
              .collect(Collectors.toList()));
    }

    if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
      fieldSearchValue.put(
          "skills",
          jobPostFilter.getSkills().stream()
              .map(
                  skill ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, skill))
              .collect(Collectors.toList()));
    }

    // filer job post active
    List<Boolean> booleanList = new ArrayList<>();
    booleanList.add(true);

    fieldSearchValue.put(
        "isActive",
        booleanList.stream()
            .map(
                boo ->
                    new ModelQuery(
                        ModelQuery.TYPE_QUERY_EQUAL, ModelQuery.TYPE_ATTRIBUTE_BOOLEAN, boo))
            .collect(Collectors.toList()));

    if (jobPostFilter.getCategoryIds() != null && !jobPostFilter.getCategoryIds().isEmpty()) {
      fieldSearchValue.put(
          "categoryEntity",
          jobPostFilter.getCategoryIds().stream()
              .map(
                  id ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_EQUAL, ModelQuery.TYPE_ATTRIBUTE_NUMBER, id))
              .collect(Collectors.toList()));
    }

    List<String> commonFieldSearch = new ArrayList<>();
    List<ModelQuery> valueCommonFieldSearch = null;
    if (jobPostFilter.getOthers() != null && !jobPostFilter.getOthers().isEmpty()) {
      valueCommonFieldSearch =
          jobPostFilter.getOthers().stream()
              .map(
                  other ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, other))
              .collect(Collectors.toList());

      commonFieldSearch.add("description");
      commonFieldSearch.add("name");
    }

    List<JobPostEntity> jobPostEntities =
        searchService.filterObject(
            "JobPostEntity",
            searchBetweenList,
            fieldSearchValue,
            fieldSearchValueSpecial,
            commonFieldSearch,
            valueCommonFieldSearch,
            jobPostFilter.getNoOfRecords(),
            jobPostFilter.getPageIndex(),
            jobPostFilter.isSortById());

    List<JobPostDto> jobPostDtos =
        jobPostEntities.stream()
            .map(
                jobPost -> {
                  JobPostDto jobPostDTO = converter.toDTO(jobPost);
                  jobPostDTO.setDescription(null);
                  return jobPostDTO;
                })
            .collect(Collectors.toList());

    // JobPostFilter jobPostFilter1 = new JobPostFilter();

    return new ResponseObject(200, "Result for filter job post", jobPostDtos);
  }

  @PostMapping("/candidate-filter")
  public ResponseObject filterCandidate(@RequestBody CandidateFilter candidateFilter) {

    Map<String, List<ModelQuery>> fieldSearchValue = new HashMap<>();
    if (candidateFilter.getEducationLevels() != null
        && !candidateFilter.getEducationLevels().isEmpty()) {
      fieldSearchValue.put(
          "educationLevel",
          candidateFilter.getEducationLevels().stream()
              .map(
                  educationLevel ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE,
                          ModelQuery.TYPE_ATTRIBUTE_STRING,
                          educationLevel))
              .collect(Collectors.toList()));
    }

    if (candidateFilter.getSkills() != null && !candidateFilter.getSkills().isEmpty()) {
      fieldSearchValue.put(
          "skills",
          candidateFilter.getSkills().stream()
              .map(
                  skill ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, skill))
              .collect(Collectors.toList()));
    }

    if (candidateFilter.getPositions() != null && !candidateFilter.getPositions().isEmpty()) {
      fieldSearchValue.put(
          "positions",
          candidateFilter.getPositions().stream()
              .map(
                  position ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, position))
              .collect(Collectors.toList()));
    }

    if (candidateFilter.getServices() != null && !candidateFilter.getServices().isEmpty()) {
      fieldSearchValue.put(
          "services",
          candidateFilter.getServices().stream()
              .map(
                  service ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, service))
              .collect(Collectors.toList()));
    }

    List<String> commonFieldSearch = new ArrayList<>();
    List<ModelQuery> valueCommonFieldSearch =
        candidateFilter.getOthers().stream()
            .map(
                other ->
                    new ModelQuery(
                        ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, other))
            .collect(Collectors.toList());

    if (candidateFilter.getOthers() != null && !candidateFilter.getOthers().isEmpty()) {
      commonFieldSearch.add("introduction");
      commonFieldSearch.add("phoneNum");
    }

    List<CandidateEntity> candidateEntity =
        searchService.filterObject(
            "CandidateEntity",
            null,
            fieldSearchValue,
            null,
            commonFieldSearch,
            valueCommonFieldSearch,
            candidateFilter.getNoOfRecords(),
            candidateFilter.getPageIndex(),
            candidateFilter.isSortById());

    List<CandidateDto> candidateDTOS =
        candidateEntity.stream()
            .map(candidate -> converter.toDTO(candidate))
            .collect(Collectors.toList());

    // CandidateFilter candidateFilter1 = new CandidateFilter();

    return new ResponseObject(200, "Result for filter candidate", candidateDTOS);
  }

  @GetMapping("/get-all-extra-info")
  public ResponseObject getAllExtraInfo() {
    return extraInfoService.getAll();
  }

  @GetMapping("/employer/get-employer-by-id/{id}")
  ResponseObject getEmployerById(@PathVariable long id) {
    return employerService.getOne(id);
  }

  @PostMapping("/register")
  public DataResponse registerAccount(@RequestBody RegisterUserDto user) {

    try {
      UserEntity userEntity = userService.registerUser(user).orElse(null);
      if (userEntity != null) {
        securityService.sendTokenVerifyAccount(userEntity.getEmail());
        return new DataResponse("Create user " + user.getEmail() + " success");
        }
      }
    catch (MessagingException | TemplateException | IOException | ExecutionException | InterruptedException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
    }
    return new DataResponse("Error while sent mail verify");
  }

  @GetMapping("/get-hot-job-post")
  public ResponseObject getHotJobPost() {
    return jobPostService.getHotJobPost();
  }

  @GetMapping("/get-job-post-due-soon")
  public ResponseObject getJobPostDueSoon() {
    return jobPostService.getJobPostDueSoon();
  }

  @GetMapping("/get-profile-candidate/{candidateId}")
  ResponseObject getProfileCandidate(@PathVariable long candidateId) {
    return candidateService.getOne(candidateId);
  }

  @GetMapping("/get-active-job-post")
  public ResponseObject getActiveJobPost() {
    return jobPostService.getActiveJobPost();
  }

  @GetMapping("/get-experience-by-candidate-id/{id}")
  ResponseObject getAllExperienceByCandidateId(@PathVariable(value = "id") long id) {

    return experienceService.getAllExperienceByCandidateId(id);
  }

  @GetMapping("/candidate-profile/{candidateId}")
  ResponseObject getCandidateProfile(@PathVariable(value = "candidateId") long candidateId) {
    return candidateService.getOne(candidateId);
  }

  @GetMapping("/get-amount-application-to-job-post/{jobPostId}")
  DataResponse getAmountApplicationToEmployer(@PathVariable(value = "jobPostId") long jobPostId) {
    return applicationService.getAmountApplicationByJobPostId(jobPostId);
  }

  @GetMapping("/get-job-post-amount")
  public ResponseObject getJobPostAmount() {
    return jobPostService.getJobPostAmount();
  }

  @GetMapping("/view-job-post/{jobPostId}")
  public DataResponse viewJobPost(@PathVariable(value = "jobPostId") long jobPostId) {
    return jobPostService.countJobPostViewReturnDataResponse(jobPostId);
  }

  @GetMapping("/get-application-amount")
  public ResponseObject getApplicationAmount() {
    return applicationService.getApplicationAmount();
  }

  @GetMapping("/view-blog-post/{blogPostId}")
  public DataResponse viewBlogPost(@PathVariable long blogPostId) {
    return blogPostService.getOneById(blogPostId);
  }

  @PostMapping("/comment/{blogPostId}")
  public DataResponse createComment(
      @RequestBody CreateCommentPayload createCommentPayload, @PathVariable long blogPostId) {
    CommentDto commentDTO = modelMapper.map(createCommentPayload, CommentDto.class);
    commentDTO.setBlogPostId(blogPostId);
    return commentService.save(commentDTO);
  }

  @GetMapping("/like-comment/{commentId}")
  public DataResponse likeComment(@PathVariable long commentId) {
    return commentService.likeComment(commentId);
  }

  @GetMapping("/dis-like-comment/{commentId}")
  public DataResponse disLikeComment(@PathVariable long commentId) {
    return commentService.disLikeComment(commentId);
  }

  @GetMapping("/blog-post")
  public DataResponse getAllBlogPost() {
    return blogPostService.getAll();
  }
}
