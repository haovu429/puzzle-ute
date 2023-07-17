package hcmute.puzzle.services.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.request.JobPostAdminPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.JobPostUserPostRequest;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.CandidateMapper;
import hcmute.puzzle.infrastructure.mappers.JobPostMapper;
import hcmute.puzzle.infrastructure.models.JobPostFilterRequest;
import hcmute.puzzle.infrastructure.models.JobPostWithApplicationAmount;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.utils.TimeUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobPostService {

	public static int LIMITED_NUMBER_OF_JOB_POSTS_CREATED_DEFAULT = 2;

	@PersistenceContext
	public EntityManager em;

	//  @Autowired
	//  Converter converter;

	@Autowired
	JobPostRepository jobPostRepository;

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	ApplicationRepository applicationRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JobPostMapper jobPostMapper;

	@Autowired
	CandidateMapper candidateMapper;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	EmployerRepository employerRepository;

	@Autowired
	JobPostViewRepository jobPostViewRepository;

	@Autowired
	JobAlertRepository jobAlertRepository;

	public JobPostDto add(JobPostUserPostRequest createJobPostRequest) {
		//validateJobPost(jobPostDTO);
		//    JobPost jobPost = JobPost.builder()
		//                             .title(createJobPostRequest.getTitle())
		//                             .position(createJobPostRequest.getPosition())
		//                             .employmentType(createJobPostRequest.getEmploymentType())
		//                             .workplaceType(createJobPostRequest.getWorkplaceType())
		//                             .description(createJobPostRequest.getDescription())
		//                             .city(createJobPostRequest.getCity())
		//                             .address(createJobPostRequest.getAddress())
		//                             .educationLevel(createJobPostRequest.getEducationLevel())
		//                             .experienceYear(createJobPostRequest.getExperienceYear())
		//                             .quantity(createJobPostRequest.getQuantity())
		//                             .minBudget(createJobPostRequest.getMinBudget())
		//                             .maxBudget(createJobPostRequest.getMaxBudget())
		//                             .deadline(createJobPostRequest.getDeadline())
		//                             .workStatus(createJobPostRequest.getWorkStatus())
		//                             .blind(createJobPostRequest.getBlind())
		//                             .deaf(createJobPostRequest.getDeaf())
		//                             .communicationDis(createJobPostRequest.getCommunicationDis())
		//                             .handDis(createJobPostRequest.getHandDis())
		//                             .labor(createJobPostRequest.getLabor())
		//                             .skills(createJobPostRequest.getSkills())
		//                             .isPublic(createJobPostRequest.getIsPublic())
		//                             .canApply(createJobPostRequest.getCanApply())
		//                             .build();
		JobPost jobPost = jobPostMapper.jobPostUserPostRequestToJobPost(createJobPostRequest);
		//Set default
		setDefault(jobPost);
		if (createJobPostRequest.getCompanyId() != null) {
			Company company = companyRepository.findById(createJobPostRequest.getCompanyId())
											   .orElseThrow(() -> new NotFoundDataException("Not found company"));
			jobPost.setCompany(company);
		}

		if (createJobPostRequest.getCategoryId() != null) {
			Category category = categoryRepository.findById(createJobPostRequest.getCategoryId())
												  .orElseThrow(() -> new NotFoundDataException("Not found category"));
			jobPost.setCategory(category);
		}
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																					   .getAuthentication()
																					   .getPrincipal();

		User user = customUserDetails.getUser();
		Employer employer = employerRepository.findById(user.getId())
											  .orElseThrow(() -> new NotFoundDataException("Not found employer"));
		jobPost.setCreatedEmployer(employer);

		// set id
		jobPost.setId(0);
		//    jobPost.setCreateTime(new Date());

		jobPost = jobPostRepository.save(jobPost);
		return jobPostMapper.jobPostToJobPostDto(jobPost);
	}

	public JobPost setDefault(JobPost jobPost) {
		jobPost.setCanApply(true);
		jobPost.setIsPublic(true);
		jobPost.setIsActive(true);
		jobPost.setBlind(false);
		jobPost.setDeaf(false);
		jobPost.setCommunicationDis(false);
		jobPost.setHandDis(false);
		jobPost.setLabor(false);
		return jobPost;
	}


	public void delete(long id) {
		JobPost jobPost = jobPostRepository.findById(id)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		jobPostRepository.delete(jobPost);
	}


	public void markJobPostWasDelete(long id) {
		JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found data"));
		jobPost.setIsDeleted(true);
		jobPost.setIsActive(false);
		jobPostRepository.markJobPostWasDelete(id);

	}


	public JobPostDto updateJobPostWithRoleUser(long jobPostId, JobPostUserPostRequest jobPostUserPostRequest) {

		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																					   .getAuthentication()
																					   .getPrincipal();
		User currentUser = customUserDetails.getUser();
		if (currentUser.getId() != jobPost.getCreatedEmployer().getId()) {
			throw new UnauthorizedException("You don't have rights for this job post");
		}
		jobPostMapper.updateJobPostFromJobPostUserPostRequest(jobPostUserPostRequest, jobPost);

		if (jobPostUserPostRequest.getCompanyId() != null ) {
			Company company = companyRepository.findById(jobPostUserPostRequest.getCompanyId()).orElseThrow(
					() -> new NotFoundDataException("Not found company")
			);
			jobPost.setCompany(company);
		}
		if (jobPostUserPostRequest.getCategoryId() != null ) {
			Category category = categoryRepository.findById(jobPostUserPostRequest.getCategoryId()).orElseThrow(
					() -> new NotFoundDataException("Not found company")
			);
			jobPost.setCategory(category);
		}

		jobPostRepository.save(jobPost);
		return jobPostMapper.jobPostToJobPostDto(jobPost);
	}


	public JobPostDto updateJobPostWithRoleAdmin(long jobPostId, JobPostAdminPostRequest jobPostAdminPostRequest) {

		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																					   .getAuthentication()
																					   .getPrincipal();
		User currentUser = customUserDetails.getUser();
		if (!currentUser.getIsAdmin() && currentUser.getId() != jobPost.getCreatedEmployer().getId()) {
			throw new UnauthorizedException("You don't have rights for this job post");
		}
		jobPostMapper.updateJobPostFromJobPostAdminPostRequest(jobPostAdminPostRequest, jobPost);
		jobPostRepository.save(jobPost);
		return jobPostMapper.jobPostToJobPostDto(jobPost);
	}


	public JobPostDto getOne(long id) {
		JobPost jobPost = jobPostRepository.findById(id)
										   .orElseThrow(() -> new NotFoundDataException(
												   "Cannot find job post with id = " + id));
		JobPostDto jobPostDto = jobPostMapper.jobPostToJobPostDto(jobPost);
		processingViewOfJobPost(jobPostDto);
		return jobPostDto;
	}

	public void processingViewOfJobPost(JobPostDto jobPostDto) {
		long viewNum = jobPostViewRepository.countByJobPostId(jobPostDto.getId());
		jobPostDto.setViews(viewNum);
	}


	public Page<JobPostDto> getAll(Pageable pageable) {
		Page<JobPost> jobPosts = jobPostRepository.findAll(pageable);

		Page<JobPostDto> jobPostDtos = jobPosts.map(jobPostMapper::jobPostToJobPostDto);

		return jobPostDtos;
	}

	//  
	//  public List<JobPostDto> getJobPostWithPage(int pageNum, int numOfRecord) {
	//
	//    Pageable pageable = PageRequest.of(pageNum, numOfRecord);
	//
	//    Page<JobPost> jobPostEntities = jobPostRepository.findAll(pageable);
	//
	//    Page<Object> jobPostDTOS = jobPostEntities.map(entity -> converter.toDTO(entity));
	//
	//    return jobPostDTOS;
	//  }

	public List<CandidateDto> getCandidatesApplyJobPost(long jobPostId) {
		List<Candidate> candidateApply = jobPostRepository.getCandidateApplyJobPost(jobPostId);
		List<CandidateDto> candidateDTOS = candidateApply.stream()
														 .map(candidateMapper::candidateToCandidateDto)
														 .toList();
		return candidateDTOS;
	}

	//  public DataResponse getJobPostCandidateApplied(long candidateId) {
	//    Set<JobPostEntity> jobPostApplied =
	// jobPostRepository.getJobPostCandidateApplied(candidateId);
	//    Set<JobPostDto> jobPostDTOS =
	//            jobPostApplied.stream()
	//                    .map(jobPost -> converter.toDTO(jobPost))
	//                    .collect(Collectors.toSet());
	//
	//    return new DataResponse(200, "Job Post applied", jobPostDTOS);
	//  }


	public List<JobPostDto> getJobPostAppliedByCandidateId(long candidateId) {
		List<JobPostDto> jobPostDtos = jobPostRepository.findAllByAppliedCandidateId(candidateId)
														.stream()
														.map(jobPostMapper::jobPostToJobPostDto)
														.toList();
		processListJobPost(jobPostDtos);
		return jobPostDtos;
	}


	public Page<JobPostWithApplicationAmount> getJobPostCreatedByEmployerId(long employerId, Pageable pageable) {
		Page<JobPostWithApplicationAmount> response =
				// Set<JobPostDto> jobPostDTOS =
				jobPostRepository.findAllByCreatedEmployerId(employerId, pageable).map(jobPostEntity -> {
					Long applicationAmount = applicationRepository.getAmountApplicationByJobPostId(
							jobPostEntity.getId());
					JobPostWithApplicationAmount jobPostWithApplicationAmount = JobPostWithApplicationAmount.builder()
																											.jobPost(
																													jobPostMapper.jobPostToJobPostDto(
																															jobPostEntity))
																											.applicationAmount(
																													applicationAmount)
																											.build();
					return jobPostWithApplicationAmount;
				});

		return response;
	}


	public List<JobPostDto> getActiveJobPost() {
		List<JobPostDto> jobPostDtos = jobPostRepository.findAllByActiveIsTrue().stream().map(jobPostEntity -> {
			JobPostDto jobPostDTO = jobPostMapper.jobPostToJobPostDto(jobPostEntity);
			jobPostDTO.setDescription(null);
			return jobPostDTO;
		}).collect(Collectors.toList());

		return jobPostDtos;
	}


	public List<JobPostDto> getInactiveJobPost() {
		List<JobPostDto> jobPostDtos = jobPostRepository.findAllByActiveIsFalse()
														.stream()
														.map(jobPostMapper::jobPostToJobPostDto)
														.toList();

		return jobPostDtos;
	}


	public List<JobPostDto> getJobPostByCreateEmployerId(long employerId, boolean isActive) {

		QJobPost jobPost = QJobPost.jobPost;
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		List<JobPost> jobPosts = queryFactory.selectFrom(jobPost)
											 .where(jobPost.isActive.eq(isActive),
													jobPost.createdEmployer.id.eq(employerId),
													jobPost.isDeleted.eq(false))
											 .fetch();
		List<JobPostDto> jobPostDtos = jobPosts.stream().map(jobPostMapper::jobPostToJobPostDto).toList();

		return jobPostDtos;
	}


	public List<JobPostDto> getJobPostSavedByCandidateId(long candidateId) {
		Candidate candidate = candidateRepository.findById(candidateId)
												 .orElseThrow(() -> new NotFoundDataException("Candidate isn't exist"));


		List<JobPostDto> jobPostDtos = candidate.getSavedJobPost()
												.stream()
												.map(jobPostMapper::jobPostToJobPostDto)
												.toList();
		processListJobPost(jobPostDtos);
		return jobPostDtos;
	}

	public void activateJobPost(long jobPostId) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		jobPost.setIsActive(true);
		jobPostRepository.save(jobPost);
	}

	public void deactivateJobPost(long jobPostId) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		jobPost.setIsActive(false);
		jobPostRepository.save(jobPost);
	}

	public void validateJobPost(JobPostUserPostRequest jobPostUserPostRequest) {
		// check budget
		if (jobPostUserPostRequest.getMinBudget() > jobPostUserPostRequest.getMaxBudget()) {
			throw new CustomException("Min budget can't be greater than max budget");
		}

		if (jobPostUserPostRequest.getDeadline() != null && jobPostUserPostRequest.getDeadline().before(new Date())) {
			throw new CustomException("Due time invalid");
		}
	}

	public List<JobPostDto> getJobPostDueSoon() {
		//    String strQuery = "SELECT jp FROM JobPost jp ORDER BY jp.deadline ASC";
		//    CustomNullsFirstInterceptor firstInterceptor = new CustomNullsFirstInterceptor();
		//    strQuery = firstInterceptor.onPrepareStatement(strQuery);
		//    List<JobPost> jobPostEntities = em.createQuery(strQuery).setMaxResults(15).getResultList();
		//
		//
		//    List<JobPostDtoOld> jobPostDto0s = jobPostEntities.stream()
		//                                                      .map(jobPost -> converter.toDTO(jobPost))
		//                                                      .collect(Collectors.toList());
		//    return new DataResponse<>(200, "Job Posts due soon", jobPostDto0s);
		QJobPost jobPost = QJobPost.jobPost;
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		List<JobPost> jobPosts = queryFactory.selectFrom(jobPost).orderBy(jobPost.deadline.desc()).limit(15).fetch();
		List<JobPostDto> jobPostDtos = jobPosts.stream().map(jobPostMapper::jobPostToJobPostDto).toList();
		return jobPostDtos;
	}

	public List<JobPostDto> getHotJobPost() {
		//        String strQuery = "SELECT jp FROM JobPost jp ORDER BY jp.createdAt DESC";
		//        CustomNullsFirstInterceptor firstInterceptor = new CustomNullsFirstInterceptor();
		//        strQuery = firstInterceptor.onPrepareStatement(strQuery);
		//        // TypedQuery q = em.createQuery(strQuery, JobPostEntity.class);

		//    List<JobPost> jobPostEntities = em.createQuery(strQuery).setMaxResults(15).getResultList();
		//
		//    List<JobPostDtoOld> jobPostDto0s = jobPostEntities.stream()
		//                                                      .map(jobPost -> converter.toDTO(jobPost))
		//                                                      .collect(Collectors.toList());
		//    return new DataResponse<>(200, " Hot Job Posts", jobPostDto0s);
		QJobPost jobPost = QJobPost.jobPost;
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		List<JobPost> jobPosts = queryFactory.selectFrom(jobPost).orderBy(jobPost.createdAt.desc()).limit(15).fetch();
		List<JobPostDto> jobPostDtos = jobPosts.stream().map(jobPostMapper::jobPostToJobPostDto).toList();
		return jobPostDtos;
	}


	public long getJobPostAmount() {
		return jobPostRepository.count();
	}


	public long getViewedJobPostAmountByUserId(long userId) {
		long amount = jobPostRepository.getViewedJobPostAmountByUser(userId);
		return amount;
	}


	public long countJobPostViewReturnDataResponse(long jobPostId) {
		return countJobPostView(jobPostId);
	}

	public long countJobPostView(long jobPostId) {
		Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
		if (jobPost.isEmpty()) {
			throw new CustomException("Cannot find job post with id = " + jobPostId);
		}
		jobPost.get().setViews(jobPost.get().getViews() + 1);
		jobPostRepository.save(jobPost.get());
		return jobPost.get().getViews();
	}


	public void viewJobPost(String email, long jobPostId) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		JobPostView jobPostView = jobPostViewRepository.findByEmailAndJobPostId(email, jobPostId).orElse(null);
		if (jobPostView == null) {
			jobPostView = JobPostView.builder().email(email).jobPostId(jobPost.getId()).build();
			jobPostViewRepository.save(jobPostView);
		} else {
			jobPostView.setUpdatedAt(new Date());
			jobPostViewRepository.save(jobPostView);
		}
	}

	public List<JobPostDto> getRecentViewJobPost(long userId) {
		List<JobPostDto> jobPostDtos = new ArrayList<>();
		try {
			jobPostDtos = jobPostRepository.getRecentViewJobPostByUserId(userId)
							 .stream()
							 .map(jobPostMapper::jobPostToJobPostDto)
							 .toList();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		processListJobPost(jobPostDtos);
		return jobPostDtos;
	}


	public double getApplicationRateByJobPostId(long jobPostId) {
		Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
		if (jobPost.isEmpty()) {
			throw new CustomException("Cannot find job post with id = " + jobPostId);
		}

		double rate = 0; // mac dinh, hoi sai neu view = 0 và application > 0
		long viewOfCandidateAmount = jobPostRepository.getViewedCandidateAmountByJobPostId(jobPostId);
		long applicationOfCandidateAmount = applicationRepository.getAmountApplicationByJobPostId(jobPostId);

		if (viewOfCandidateAmount != 0 && viewOfCandidateAmount >= applicationOfCandidateAmount) {
			rate = Double.valueOf(applicationOfCandidateAmount) / viewOfCandidateAmount;
			rate = rate * 100; // doi ti le ra phan tram
			// lam tron 2 chu so thap phan
			rate = Math.round(rate * 100.0) / 100.0;
		}

		return rate;
	}

	public long getLimitNumberOfJobPostsCreatedForEmployer(long employerId) {
		long limitNum = 0;
		// check subscribes of employer
		String sql = "SELECT SUM(pack.numOfJobPost) FROM Subscription sub, Package pack, User u WHERE sub.aPackage.id = pack.id " + "AND sub.regUser.id = u.id AND u.id=:userId AND sub.expirationTime > :nowTime ";
		try {
			limitNum = (long) em.createQuery(sql)
								.setParameter("userId", employerId)
								.setParameter("nowTime", new Date())
								.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return LIMITED_NUMBER_OF_JOB_POSTS_CREATED_DEFAULT + limitNum;
	}


	public long getTotalJobPostViewOfEmployer(long employerId) {
		return jobPostRepository.getTotalJobPostViewOfEmployer(employerId);
	}

	public long getCurrentNumberOfJobPostsCreatedByEmployer(long employerId) {
		long count = 0;
		// check subscribes of employer
		String sql = "SELECT COUNT(jp) FROM JobPost jp WHERE jp.createdEmployer.id = :employerId AND jp.isDeleted = FALSE";
		try {
			count = (long) em.createQuery(sql).setParameter("employerId", employerId).getSingleResult();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}

	public Page<JobPostDto> filterJobPostByJobAlert(JobAlert jobAlert, Pageable pageable) {

		JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder()
																		.isActive(true)
																		.searchKeys(List.of(jobAlert.getTag(),
																							jobAlert.getIndustry()))
																		.canApply(true)
																		.build();
		Specification<JobPost> jobPostSpecification = doPredicate(jobPostFilterRequest);
		Page<JobPostDto> jobPostDtos = jobPostRepository.findAll(jobPostSpecification, pageable)
														.map(jobPostMapper::jobPostToJobPostDto);

		return jobPostDtos;
	}

	public List<JobPostDto> filterJobPostByJobAlert(JobAlert jobAlert) {

		JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder()
																		.isActive(true)
																		.searchKeys(List.of(jobAlert.getTag(),
																							jobAlert.getIndustry()))
																		.canApply(true)
																		.build();
		Specification<JobPost> jobPostSpecification = doPredicate(jobPostFilterRequest);
		Page<JobPostDto> jobPostDtos = jobPostRepository.findAll(jobPostSpecification, Pageable.unpaged())
														.map(jobPostMapper::jobPostToJobPostDto);
		List<JobPostDto> jobPostDtoList = jobPostDtos.getContent();

		return jobPostDtoList;
	}

	public List<JobPostDto> filterJobPostByAllJobAlert(long userId) {
		List<JobPostDto> jobPostDtos = new ArrayList<>();
		Candidate candidate = candidateRepository.findById(userId).orElse(null);
		if (candidate != null) {
			List<JobAlert> jobAlerts = jobAlertRepository.findAllByCandidate_Id(userId);
			for (JobAlert jobAlert : jobAlerts) {
				List<JobPostDto> temp = new ArrayList<>(this.filterJobPostByJobAlert(jobAlert));
				// Avoid duplicate
				temp.removeAll(jobPostDtos);
				jobPostDtos.addAll(temp);
			}
		}
		return jobPostDtos;
	}

	//  @Scheduled(cron = "0 0 0 * * ?")
	//  public Page<JobPostDto> cronJobFilterJobPostByJobAlert(JobAlert jobAlert, Pageable pageable) {
	//
	//    JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder()
	//                                                                    .city(jobAlert.getCity())
	//                                                                    .isActive(true)
	//                                                                    .searchKeys(List.of(jobAlert.getTag(),
	//                                                                                        jobAlert.getIndustry()))
	//                                                                    .minBudget(jobAlert.getMinBudget())
	//                                                                    .canApply(true)
	//                                                                    .position(jobAlert.getTag())
	//
	//                                                                    .build();
	//    Specification<JobPost> jobPostSpecification = doPredicate(jobPostFilterRequest);
	//    Page<JobPostDto> jobPostDtos = jobPostRepository.findAll(jobPostSpecification, pageable)
	//                                                    .map(jobPostMapper::jobPostToJobPostDto);
	//
	//    return jobPostDtos;
	//  }

	public void checkCreatedJobPostLimit(long employerId) {
		long limit = getLimitNumberOfJobPostsCreatedForEmployer(employerId);
		long current = getCurrentNumberOfJobPostsCreatedByEmployer(employerId);

		if (current > limit) {
			throw new CustomException(
					"Your current number of created jobs is" + current + ", which has exceeded the limit of " + limit);
		}
	}

	public void processListJobPost(Collection<JobPostDto> jobPostDtos) {
		jobPostDtos.forEach(jobPostDTO -> {
			jobPostDTO.setDescription(null);
			processingViewOfJobPost(jobPostDTO);
		});
	}

	public Page<JobPostDto> filterJobPost(Pageable pageable){
		JobPostFilterRequest jobPostFilterRequest = JobPostFilterRequest.builder().sortColumn("updatedAt").build();
		return filterJobPost(jobPostFilterRequest, pageable);
	}

	public Page<JobPostDto> filterJobPost(JobPostFilterRequest jobPostFilterRequest, Pageable pageable) {
		JobPostFilterRequest jobPostFilter = jobPostFilterRequest;
		if (jobPostFilter.getNumDayAgo() != null && jobPostFilter.getNumDayAgo() != -1 && jobPostFilter.getNumDayAgo() > 0) {
			TimeUtil timeUtil = new TimeUtil();
			Date timeLine = timeUtil.upDownTime_TimeUtil(new Date(), jobPostFilter.getNumDayAgo(), 0, 0);
			jobPostFilter.setCreatedAtFrom(timeLine);
		}
		Specification<JobPost> jobPostSpecification = doPredicate(jobPostFilter);
		Page<JobPostDto> jobPostDtos = jobPostRepository.findAll(jobPostSpecification, pageable)
														.map(jobPostMapper::jobPostToJobPostDto);

		return jobPostDtos;
	}


	public Specification<JobPost> doPredicate(JobPostFilterRequest jobPostFilter) {

		return ((root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new LinkedList<>();

			Predicate withCheckDeletedFromSystem = criteriaBuilder.equal(root.get("isDeleted"), false);
			predicates.add(withCheckDeletedFromSystem);

			// Is Active
			if (jobPostFilter.getIsActive() != null) {
				Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isActive"),
																			jobPostFilter.getIsActive());
				predicates.add(withCheckActiveFromSystem);
			}

			// Is Public
			if (jobPostFilter.getIsActive() != null) {
				Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isPublic"),
																			jobPostFilter.getIsPublic());
				predicates.add(withCheckActiveFromSystem);
			}

			// Category
			if (jobPostFilter.getCategoryIds() != null && !jobPostFilter.getCategoryIds().isEmpty()) {
				Join<JobPost, Category> categoryJoin = root.join("category", JoinType.INNER);
				predicates.add(categoryJoin.get("id").in(jobPostFilter.getCategoryIds()));
			}

			// Company
			if (jobPostFilter.getCompanyIds() != null && !jobPostFilter.getCompanyIds().isEmpty()) {
				Join<JobPost, Company> companyJoin = root.join("company", JoinType.INNER);
				predicates.add(companyJoin.get("id").in(jobPostFilter.getCompanyIds()));
			}

			// EmployerType
			if (jobPostFilter.getEmploymentTypes() != null && !jobPostFilter.getEmploymentTypes().isEmpty()) {
				Predicate inEmploymentType = root.get("employmentType").in(jobPostFilter.getEmploymentTypes());
				predicates.add(criteriaBuilder.and(inEmploymentType));
			}

			// ExperienceYear
			if (jobPostFilter.getExperienceYear() != null) {
				Predicate lessExperienceYear = criteriaBuilder.lessThanOrEqualTo(root.get("experienceYear"),
																				 jobPostFilter.getExperienceYear());
				Predicate withExperienceYearNull = criteriaBuilder.isNull(root.get("experienceYear"));
				predicates.add(criteriaBuilder.or(lessExperienceYear, withExperienceYearNull));
			}

			// Min budget
			if (jobPostFilter.getMinBudget() != null) {
				Predicate greaterMinBudget = criteriaBuilder.greaterThanOrEqualTo(root.get("minBudget"),
																				  jobPostFilter.getMinBudget());
				predicates.add(criteriaBuilder.and(greaterMinBudget));
			}

			// MaxBudget
			if (jobPostFilter.getMaxBudget() != null) {
				Predicate lessMaxBudget = criteriaBuilder.lessThanOrEqualTo(root.get("maxBudget"),
																			jobPostFilter.getMaxBudget());
				predicates.add(criteriaBuilder.and(lessMaxBudget));
			}

			// City
			if (jobPostFilter.getCity() != null) {
				Predicate inEmploymentType = criteriaBuilder.like(criteriaBuilder.lower(root.get("city")),
																  "%" + jobPostFilter.getCity().toLowerCase() + "%");
				predicates.add(criteriaBuilder.and(inEmploymentType));
			}

			// Position
			if (jobPostFilter.getPosition() != null) {
				Predicate inEmploymentType = criteriaBuilder.like(criteriaBuilder.lower(root.get("position")),
																  "%" + jobPostFilter.getPosition()
																					 .toLowerCase() + "%");
				predicates.add(criteriaBuilder.and(inEmploymentType));
			}

			// Skill
			if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
				List<Predicate> or = new ArrayList<>();
				for (String skill : jobPostFilter.getSkills()) {
					Predicate orSkill = criteriaBuilder.like(criteriaBuilder.lower(root.get("skills")),
															 "%" + skill + "%");
					or.add(orSkill);
				}
				predicates.add(criteriaBuilder.or(or.toArray(new Predicate[0])));
			}

			// Search Key
			if (jobPostFilter.getSearchKeys() != null && !jobPostFilter.getSearchKeys().isEmpty()) {
				List<Predicate> or = new ArrayList<>();
				for (String keyword : jobPostFilter.getSearchKeys()) {
					Predicate orInTitle = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
															   "%" + keyword + "%");
					Predicate orInDescription = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
																	 "%" + keyword + "%");
					or.add(orInTitle);
					or.add(orInDescription);
				}
				predicates.add(criteriaBuilder.or(or.toArray(new Predicate[0])));
			}

			// CanApply
			if (jobPostFilter.getCanApply() != null && jobPostFilter.getCanApply()) {
				Date date = new Date();
				Timestamp ts = new Timestamp(date.getTime());

				Predicate withCheckEnableApply = criteriaBuilder.equal(root.get("canApply"),
																	   jobPostFilter.getCanApply());

				Predicate orPredicate;
				Predicate withCheckDeadlineGreater = criteriaBuilder.greaterThanOrEqualTo(root.get("deadline"), ts);
				Predicate withCheckDeadlineNull = criteriaBuilder.isNull(root.get("deadline"));
				orPredicate = criteriaBuilder.or(withCheckDeadlineGreater, withCheckDeadlineNull);
				Predicate finalPredicate = criteriaBuilder.and(withCheckEnableApply, orPredicate);
				predicates.add(criteriaBuilder.and(finalPredicate));
			}

			//Create Time
			if (jobPostFilter.getCreatedAtFrom() != null) {
				Timestamp tsCreatedAtFrom = new Timestamp(jobPostFilter.getCreatedAtFrom().getTime());
				Predicate withCreatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),
																				   tsCreatedAtFrom);
				predicates.add(criteriaBuilder.and(withCreatedAtFrom));
			}

			if (jobPostFilter.getCreatedAtTo() != null) {
				Timestamp tsCreatedAtTo = new Timestamp(jobPostFilter.getCreatedAtTo().getTime());
				Predicate withCreatedAtTo = criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), tsCreatedAtTo);
				predicates.add(criteriaBuilder.and(withCreatedAtTo));
			}

			// Update time
			if (jobPostFilter.getUpdatedAtFrom() != null) {
				Timestamp tsUpdatedAtFrom = new Timestamp(jobPostFilter.getUpdatedAtFrom().getTime());
				Predicate withUpdatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"),
																				   tsUpdatedAtFrom);
				predicates.add(criteriaBuilder.and(withUpdatedAtFrom));
			}

			if (jobPostFilter.getUpdatedAtTo() != null) {
				Timestamp tsUpdatedAtTo = new Timestamp(jobPostFilter.getUpdatedAtTo().getTime());
				Predicate withUpdatedAtTo = criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), tsUpdatedAtTo);
				predicates.add(criteriaBuilder.and(withUpdatedAtTo));
			}

			// Sort
			if (Objects.nonNull(jobPostFilter.getIsAscSort()) && jobPostFilter.getIsAscSort()
																			  .equals(true) && jobPostFilter.getSortColumn() != null && !jobPostFilter.getSortColumn()
																																					  .isBlank()) {
				switch (jobPostFilter.getSortColumn()) {
					case "createdAt":
						jobPostFilter.setSortColumn("createdAt");
						query.orderBy(criteriaBuilder.asc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "updatedAt":
						jobPostFilter.setSortColumn("updatedAt");
						query.orderBy(criteriaBuilder.asc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "deadline":
						jobPostFilter.setSortColumn("deadline");
						query.orderBy(criteriaBuilder.asc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "minBudget":
						jobPostFilter.setSortColumn("minBudget");
						query.orderBy(criteriaBuilder.asc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "maxBudget":
						jobPostFilter.setSortColumn("maxBudget");
						query.orderBy(criteriaBuilder.asc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "quantity":
						jobPostFilter.setSortColumn("quantity");
						query.orderBy(criteriaBuilder.asc(root.get(jobPostFilter.getSortColumn())));
						break;
					default:
						query.orderBy(criteriaBuilder.asc(root.get(jobPostFilter.getSortColumn())));
				}
			} else if (Objects.nonNull(jobPostFilter.getIsAscSort()) && jobPostFilter.getIsAscSort()
																					 .equals(false) && jobPostFilter.getSortColumn() != null && !jobPostFilter.getSortColumn()
																																							 .isBlank()) {
				switch (jobPostFilter.getSortColumn()) {
					case "createdAt":
						jobPostFilter.setSortColumn("createdAt");
						query.orderBy(criteriaBuilder.desc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "updatedAt":
						jobPostFilter.setSortColumn("updatedAt");
						query.orderBy(criteriaBuilder.desc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "deadline":
						jobPostFilter.setSortColumn("deadline");
						query.orderBy(criteriaBuilder.desc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "minBudget":
						jobPostFilter.setSortColumn("minBudget");
						query.orderBy(criteriaBuilder.desc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "maxBudget":
						jobPostFilter.setSortColumn("maxBudget");
						query.orderBy(criteriaBuilder.desc(root.get(jobPostFilter.getSortColumn())));
						break;
					case "quantity":
						jobPostFilter.setSortColumn("quantity");
						query.orderBy(criteriaBuilder.desc(root.get(jobPostFilter.getSortColumn())));
						break;
					default:
						query.orderBy(criteriaBuilder.desc(root.get(jobPostFilter.getSortColumn())));
				}
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
		});
	}


}
