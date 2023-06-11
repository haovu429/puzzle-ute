package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.JobPostDtoOld;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.RequestPageable;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.Category;
import hcmute.puzzle.infrastructure.entities.JobPost;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.JobPostMapper;
import hcmute.puzzle.infrastructure.models.JobPostFilterRequest;
import hcmute.puzzle.infrastructure.models.JobPostWithApplicationAmount;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.JobPostService;
//import hcmute.puzzle.utils.CustomNullsFirstInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.criteria.Join;
//import javax.persistence.criteria.JoinType;
//import javax.persistence.criteria.Predicate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobPostServiceImpl implements JobPostService {

  public static int LIMITED_NUMBER_OF_JOB_POSTS_CREATED_DEFAULT = 2;

  @PersistenceContext
  public EntityManager em;

  @Autowired
  Converter converter;

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

  public ResponseObject add(JobPostDtoOld jobPostDTO) {
    validateJobPost(jobPostDTO);
    JobPost jobPost = converter.toEntity(jobPostDTO);

    // set id
    jobPost.setId(0);
    //    jobPost.setCreateTime(new Date());

    jobPost = jobPostRepository.save(jobPost);
    return new ResponseObject<>(200, "Save job post Successfully", converter.toDTO(jobPost));
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = jobPostRepository.existsById(id);
    if (exists) {
      jobPostRepository.deleteById(id);
      return new ResponseObject(200, "Delete job post Successfully", null);
    }
    throw new CustomException("Cannot find job post with id =" + id);
  }

  @Override
  public DataResponse markJobPostWasDelete(long id) {
    boolean exists = jobPostRepository.existsById(id);
    if (exists) {
      jobPostRepository.markJobPostWasDelete(id);
      return new DataResponse<>("Delete job post Successfully");
    }
    throw new CustomException("Cannot find job post with id =" + id);
  }

  @Override
  public ResponseObject update(JobPostDtoOld JobPostDTO) {

    boolean exists = jobPostRepository.existsById(JobPostDTO.getId());

    if (exists) {
      JobPost jobPost = converter.toEntity(JobPostDTO);
      // candidate.setId(candidate.getUserEntity().getId());

      jobPostRepository.save(jobPost);
      return new ResponseObject<>(converter.toDTO(jobPost));
    }

    throw new CustomException("Cannot find job post with id = " + JobPostDTO.getId());
  }

  @Override
  public ResponseObject getOne(long id) {
    boolean exists = jobPostRepository.existsById(id);

    if (exists) {
      JobPost candidate = jobPostRepository.getReferenceById(id);
      return new ResponseObject<>(200, "Info of job post", converter.toDTO(candidate));
    }

    throw new CustomException("Cannot find job post with id = " + id);
  }

  @Override
  public ResponseObject getAll() {
    List<JobPost> jobPostEntities = new ArrayList<>();

    jobPostEntities = jobPostRepository.findAll();

    List<JobPostDtoOld> jobPostDtos = jobPostEntities.stream().map(entity -> {
      return converter.toDTO(entity);
    }).collect(Collectors.toList());

    return new ResponseObject<>(200, "Info of job post", jobPostDtos);
  }

  @Override
  public ResponseObject getJobPostWithPage(int pageNum, int numOfRecord) {

    Pageable pageable = PageRequest.of(pageNum, numOfRecord);

    Page<JobPost> jobPostEntities = jobPostRepository.findAll(pageable);

    Page<Object> jobPostDTOS = jobPostEntities.map(entity -> converter.toDTO(entity));

    return new ResponseObject<>(200, "Info of job post", jobPostDTOS);
  }

  public ResponseObject getCandidatesApplyJobPost(long jobPostId) {
    Set<Candidate> candidateApply = jobPostRepository.getCandidateApplyJobPost(jobPostId);
    Set<CandidateDto> candidateDTOS = candidateApply.stream()
                                                    .map(candidate -> converter.toDTO(candidate))
                                                    .collect(Collectors.toSet());

    return new ResponseObject<>(200, "Candidate applied", candidateDTOS);
  }

  //  public ResponseObject getJobPostCandidateApplied(long candidateId) {
  //    Set<JobPostEntity> jobPostApplied =
  // jobPostRepository.getJobPostCandidateApplied(candidateId);
  //    Set<JobPostDto> jobPostDTOS =
  //            jobPostApplied.stream()
  //                    .map(jobPost -> converter.toDTO(jobPost))
  //                    .collect(Collectors.toSet());
  //
  //    return new ResponseObject(200, "Job Post applied", jobPostDTOS);
  //  }

  @Override
  public ResponseObject getJobPostAppliedByCandidateId(long candidateId) {
    Set<JobPostDtoOld> jobPostDtos = jobPostRepository.findAllByAppliedCandidateId(candidateId)
                                                      .stream()
                                                      .map(jobPostEntity -> converter.toDTO(jobPostEntity))
                                                      .collect(Collectors.toSet());
    processListJobPost(jobPostDtos);

    return new ResponseObject<>(200, "Job Post applied", jobPostDtos);
  }

  @Override
  public Page<JobPostWithApplicationAmount> getJobPostCreatedByEmployerId(long employerId, Pageable pageable) {
    Page<JobPostWithApplicationAmount> response =
            // Set<JobPostDto> jobPostDTOS =
            jobPostRepository.findAllByCreatedEmployerId(employerId, pageable).map(jobPostEntity -> {
              Long applicationAmount = applicationRepository.getAmountApplicationByJobPostId(jobPostEntity.getId());
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

  @Override
  public ResponseObject getActiveJobPost() {
    Set<JobPostDtoOld> jobPostDtos = jobPostRepository.findAllByActiveIsTrue().stream().map(jobPostEntity -> {
      JobPostDtoOld jobPostDTO = converter.toDTO(jobPostEntity);
      jobPostDTO.setDescription(null);
      return jobPostDTO;
    }).collect(Collectors.toSet());

    return new ResponseObject<>(200, "Job Post active", jobPostDtos);
  }

  @Override
  public ResponseObject getInactiveJobPost() {
    Set<JobPostDtoOld> jobPostDtos = jobPostRepository.findAllByActiveIsFalse()
                                                      .stream()
                                                      .map(jobPostEntity -> converter.toDTO(jobPostEntity))
                                                      .collect(Collectors.toSet());

    return new ResponseObject<>(200, "Job Post inactive", jobPostDtos);
  }

  public ResponseObject getActiveJobPostByCreateEmployerId(long employerId) {
    Set<JobPostDto> jobPostDtos = jobPostRepository.findAllByActiveAndCreatedEmployerId(true, employerId)
                                                   .stream()
                                                   .map(jobPostEntity -> jobPostMapper.jobPostToJobPostDto(
                                                           jobPostEntity))
                                                   .collect(Collectors.toSet());

    return new ResponseObject<>(200, "Job Post active", jobPostDtos);
  }

  @Override
  public ResponseObject getInactiveJobPostByCreateEmployerId(long employerId) {
    Set<JobPostDtoOld> jobPostDtos = jobPostRepository.findAllByActiveAndCreatedEmployerId(false, employerId)
                                                      .stream()
                                                      .map(jobPostEntity -> converter.toDTO(jobPostEntity))
                                                      .collect(Collectors.toSet());

    return new ResponseObject<>(200, "Job Post inactive", jobPostDtos);
  }

  @Override
  public ResponseObject getJobPostSavedByCandidateId(long candidateId) {
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    Set<JobPostDtoOld> jobPostDtos = candidate.get()
                                              .getSavedJobPost()
                                              .stream()
                                              .map(jobPost -> converter.toDTO(jobPost))
                                              .collect(Collectors.toSet());
    processListJobPost(jobPostDtos);

    return new ResponseObject<>(200, "Job Posts is saved", jobPostDtos);
  }

  public ResponseObject activateJobPost(long jobPostId) {
    JobPost jobPost = jobPostRepository.findById(jobPostId)
            .orElseThrow(() -> new NotFoundDataException("Not found job post"));
    jobPost.setIsActive(true);
    jobPostRepository.save(jobPost);
    return new ResponseObject(200, "Activate success", null);
  }

  public ResponseObject deactivateJobPost(long jobPostId) {
    Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
    jobPost.get().setIsActive(false);
    jobPostRepository.save(jobPost.get());
    return new ResponseObject(200, "Deactivate success", null);
  }

  public void validateJobPost(JobPostDtoOld jobPostDTO) {
    // check budget
    if (jobPostDTO.getMinBudget() > jobPostDTO.getMaxBudget()) {
      throw new CustomException("Min budget can't be greater than max budget");
    }

    if (jobPostDTO.getDueTime() != null && jobPostDTO.getDueTime().before(new Date())) {
      throw new CustomException("Due time invalid");
    }
  }

  public ResponseObject getJobPostDueSoon() {
//    String strQuery = "SELECT jp FROM JobPost jp ORDER BY jp.deadline ASC";
//    CustomNullsFirstInterceptor firstInterceptor = new CustomNullsFirstInterceptor();
//    strQuery = firstInterceptor.onPrepareStatement(strQuery);
//    List<JobPost> jobPostEntities = em.createQuery(strQuery).setMaxResults(15).getResultList();
//
//
//    List<JobPostDtoOld> jobPostDto0s = jobPostEntities.stream()
//                                                      .map(jobPost -> converter.toDTO(jobPost))
//                                                      .collect(Collectors.toList());
//    return new ResponseObject<>(200, "Job Posts due soon", jobPostDto0s);
    return null;
  }

  public ResponseObject getHotJobPost() {
//    String strQuery = "SELECT jp FROM JobPost jp ORDER BY jp.createdAt DESC";
//    CustomNullsFirstInterceptor firstInterceptor = new CustomNullsFirstInterceptor();
//    strQuery = firstInterceptor.onPrepareStatement(strQuery);
//    // TypedQuery q = em.createQuery(strQuery, JobPostEntity.class);
//
//    List<JobPost> jobPostEntities = em.createQuery(strQuery).setMaxResults(15).getResultList();
//
//    List<JobPostDtoOld> jobPostDto0s = jobPostEntities.stream()
//                                                      .map(jobPost -> converter.toDTO(jobPost))
//                                                      .collect(Collectors.toList());
//    return new ResponseObject<>(200, " Hot Job Posts", jobPostDto0s);
    return null;
  }

  @Override
  public ResponseObject getJobPostAmount() {
    long amount = jobPostRepository.count();

    return new ResponseObject<>(200, "Job Post amount", amount);
  }

  @Override
  public DataResponse getViewedJobPostAmountByUserId(long userId) {
    long amount = jobPostRepository.getViewedJobPostAmountByUser(userId);

    return new DataResponse<>(amount);
  }

  @Override
  public DataResponse countJobPostViewReturnDataResponse(long jobPostId) {
    return new DataResponse<>(countJobPostView(jobPostId));
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

  @Override
  public DataResponse viewJobPost(long userId, long jobPostId) {
    Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
    if (jobPost.isEmpty()) {
      throw new CustomException("Cannot find job post with id = " + jobPostId);
    }

    Optional<User> userEntity = userRepository.findById(userId);
    if (userEntity.isEmpty()) {
      throw new CustomException("Not found user");
    }

    jobPost.get().getViewedUsers().add(userEntity.get());
    jobPostRepository.save(jobPost.get());

    return new DataResponse(null);
  }

  @Override
  public DataResponse getApplicationRateByJobPostId(long jobPostId) {
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

    return new DataResponse<>(rate);
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


  @Override
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

  public void checkCreatedJobPostLimit(long employerId) {
    long limit = getLimitNumberOfJobPostsCreatedForEmployer(employerId);
    long current = getCurrentNumberOfJobPostsCreatedByEmployer(employerId);

    if (current > limit) {
      throw new CustomException(
              "Your current number of created jobs is" + current + ", which has exceeded the limit of " + limit);
    }
  }

  public static void processListJobPost(Collection<JobPostDtoOld> jobPostDtos) {
    jobPostDtos.forEach(jobPostDTO -> {
      jobPostDTO.setDescription(null);
    });
  }

  public Page<JobPost> filterJobPost(RequestPageable<JobPostFilterRequest> jobPostFilterRequest) {
    JobPostFilterRequest jobPostFilter = jobPostFilterRequest.getBody();
    jobPostFilter.setIsActive(true);
    Specification<JobPost> jobPostSpecification = doPredicate(jobPostFilter);
    Pageable pageable = PageRequest.of(jobPostFilterRequest.getPagination().getPage(),
                                       jobPostFilterRequest.getPagination().getSize());
    Page<JobPost> jobPostPage = jobPostRepository.findAll(jobPostSpecification, pageable);

    return jobPostPage;
  }



  public Specification<JobPost> doPredicate(JobPostFilterRequest jobPostFilter) {

    return ((root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new LinkedList<>();

      // Is Active
      if (jobPostFilter.getIsActive() != null) {
        Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isActive"), jobPostFilter.getIsActive());
        predicates.add(withCheckActiveFromSystem);
      }

      // Is Public
      if (jobPostFilter.getIsActive() != null) {
        Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isPublic"), jobPostFilter.getIsPublic());
        predicates.add(withCheckActiveFromSystem);
      }

      // Category
      if (jobPostFilter.getCategoryIds() != null && !jobPostFilter.getCategoryIds().isEmpty()) {
        Join<JobPost, Category> categoryJoin = root.join("category", JoinType.INNER);
        predicates.add(categoryJoin.get("id").in(jobPostFilter.getCategoryIds()));
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
                                                          "%" + jobPostFilter.getPosition().toLowerCase() + "%");
        predicates.add(criteriaBuilder.and(inEmploymentType));
      }

      // Skill
      if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
        List<Predicate> or = new ArrayList<>();
        for (String skill : jobPostFilter.getSkills()) {
          Predicate orSkill = criteriaBuilder.like(criteriaBuilder.lower(root.get("skills")), "%" + skill + "%");
          or.add(orSkill);
        }
        predicates.add(criteriaBuilder.or(or.toArray(new Predicate[0])));
      }

      // Search Key
      if (jobPostFilter.getSearchKeys() != null && !jobPostFilter.getSearchKeys().isEmpty()) {
        List<Predicate> or = new ArrayList<>();
        for (String keyword : jobPostFilter.getSearchKeys()) {
          Predicate orInTitle = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword + "%");
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

        Predicate withCheckEnableApply = criteriaBuilder.equal(root.get("canApply"), jobPostFilter.getCanApply());

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
        Predicate withCreatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), tsCreatedAtFrom);
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
        Predicate withUpdatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), tsUpdatedAtFrom);
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
                                                                               .equals(true) && jobPostFilter.getSortColumn() != null && !jobPostFilter.getSortColumn()
                                                                                                                                                       .isBlank()) {
        switch (jobPostFilter.getSortColumn()) {
          case "createdAt":
            jobPostFilter.setSortColumn("createdAt");
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
