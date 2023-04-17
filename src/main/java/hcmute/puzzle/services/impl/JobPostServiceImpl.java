package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.JobPostService;
import hcmute.puzzle.utils.CustomNullsFirstInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobPostServiceImpl implements JobPostService {

  public static int LIMITED_NUMBER_OF_JOB_POSTS_CREATED_DEFAULT = 2;

  @PersistenceContext public EntityManager em;

  @Autowired Converter converter;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired ApplicationRepository applicationRepository;

  @Autowired UserRepository userRepository;

  public ResponseObject add(JobPostDTO jobPostDTO) {
    validateJobPost(jobPostDTO);
    JobPostEntity jobPostEntity = converter.toEntity(jobPostDTO);

    // set id
    jobPostEntity.setId(0);
    jobPostEntity.setCreateTime(new Date());

    jobPostEntity = jobPostRepository.save(jobPostEntity);

    return new ResponseObject(200, "Save job post Successfully", converter.toDTO(jobPostEntity));
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
      return new DataResponse("Delete job post Successfully");
    }
    throw new CustomException("Cannot find job post with id =" + id);
  }

  @Override
  public ResponseObject update(JobPostDTO JobPostDTO) {

    boolean exists = jobPostRepository.existsById(JobPostDTO.getId());

    if (exists) {
      JobPostEntity jobPost = converter.toEntity(JobPostDTO);
      // candidate.setId(candidate.getUserEntity().getId());

      jobPostRepository.save(jobPost);
      return new ResponseObject(converter.toDTO(jobPost));
    }

    throw new CustomException("Cannot find job post with id = " + JobPostDTO.getId());
  }

  @Override
  public ResponseObject getOne(long id) {
    boolean exists = jobPostRepository.existsById(id);

    if (exists) {
      JobPostEntity candidate = jobPostRepository.getReferenceById(id);
      return new ResponseObject(200, "Info of job post", converter.toDTO(candidate));
    }

    throw new CustomException("Cannot find job post with id = " + id);
  }

  @Override
  public ResponseObject getAll() {
    List<JobPostEntity> jobPostEntities = new ArrayList<>();

    jobPostEntities = jobPostRepository.findAll();

    List<JobPostDTO> jobPostDTOS =
        jobPostEntities.stream()
            .map(
                entity -> {
                  return converter.toDTO(entity);
                })
            .collect(Collectors.toList());

    return new ResponseObject(200, "Info of job post", jobPostDTOS);
  }

  @Override
  public ResponseObject getJobPostWithPage(int pageNum, int numOfRecord) {

    Pageable pageable = PageRequest.of(pageNum, numOfRecord);

    Page<JobPostEntity> jobPostEntities = jobPostRepository.findAll(pageable);

    Page<Object> jobPostDTOS = jobPostEntities.map(entity -> converter.toDTO(entity));

    return new ResponseObject(200, "Info of job post", jobPostDTOS);
  }

  public ResponseObject getCandidatesApplyJobPost(long jobPostId) {
    Set<CandidateEntity> candidateApply = jobPostRepository.getCandidateApplyJobPost(jobPostId);
    Set<CandidateDTO> candidateDTOS =
        candidateApply.stream()
            .map(candidate -> converter.toDTO(candidate))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Candidate applied", candidateDTOS);
  }

  //  public ResponseObject getJobPostCandidateApplied(long candidateId) {
  //    Set<JobPostEntity> jobPostApplied =
  // jobPostRepository.getJobPostCandidateApplied(candidateId);
  //    Set<JobPostDTO> jobPostDTOS =
  //            jobPostApplied.stream()
  //                    .map(jobPost -> converter.toDTO(jobPost))
  //                    .collect(Collectors.toSet());
  //
  //    return new ResponseObject(200, "Job Post applied", jobPostDTOS);
  //  }

  @Override
  public ResponseObject getJobPostAppliedByCandidateId(long candidateId) {
    Set<JobPostDTO> jobPostDTOS =
        jobPostRepository.findAllByAppliedCandidateId(candidateId).stream()
            .map(jobPostEntity -> converter.toDTO(jobPostEntity))
            .collect(Collectors.toSet());
    processListJobPost(jobPostDTOS);

    return new ResponseObject(200, "Job Post applied", jobPostDTOS);
  }

  @Override
  public ResponseObject getJobPostCreatedByEmployerId(long employerId) {
    List<Map<String, Object>> response = new ArrayList<>();
    // Set<JobPostDTO> jobPostDTOS =
    jobPostRepository.findAllByCreatedEmployerId(employerId).stream()
        .forEach(
            jobPostEntity -> {
              Map<String, Object> items = new HashMap<>();
              items.put("job_post", converter.toDTO(jobPostEntity));
              items.put(
                  "application_amount",
                  applicationRepository.getAmountApplicationByJobPostId(jobPostEntity.getId()));
              response.add(items);
            });

    return new ResponseObject(200, "Job Post created", response);
  }

  @Override
  public ResponseObject getActiveJobPost() {
    Set<JobPostDTO> jobPostDTOS =
        jobPostRepository.findAllByActiveIsTrue().stream()
            .map(jobPostEntity -> {
              JobPostDTO jobPostDTO = converter.toDTO(jobPostEntity);
              jobPostDTO.setDescription(null);
              return jobPostDTO;
            })
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Job Post active", jobPostDTOS);
  }

  @Override
  public ResponseObject getInactiveJobPost() {
    Set<JobPostDTO> jobPostDTOS =
        jobPostRepository.findAllByActiveIsFalse().stream()
            .map(jobPostEntity -> converter.toDTO(jobPostEntity))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Job Post inactive", jobPostDTOS);
  }

  public ResponseObject getActiveJobPostByCreateEmployerId(long employerId) {
    Set<JobPostDTO> jobPostDTOS =
        jobPostRepository.findAllByActiveAndCreatedEmployerId(true, employerId).stream()
            .map(jobPostEntity -> converter.toDTO(jobPostEntity))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Job Post active", jobPostDTOS);
  }

  @Override
  public ResponseObject getInactiveJobPostByCreateEmployerId(long employerId) {
    Set<JobPostDTO> jobPostDTOS =
        jobPostRepository.findAllByActiveAndCreatedEmployerId(false, employerId).stream()
            .map(jobPostEntity -> converter.toDTO(jobPostEntity))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Job Post inactive", jobPostDTOS);
  }

  @Override
  public ResponseObject getJobPostSavedByCandidateId(long candidateId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    Set<JobPostDTO> jobPostDTOS =
        candidate.get().getSavedJobPost().stream()
            .map(jobPost -> converter.toDTO(jobPost))
            .collect(Collectors.toSet());
    processListJobPost(jobPostDTOS);

    return new ResponseObject(200, "Job Posts is saved",jobPostDTOS);
  }

  public ResponseObject activateJobPost(long jobPostId) {
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    jobPost.get().setActive(true);
    jobPostRepository.save(jobPost.get());
    return new ResponseObject(200, "Activate success", null);
  }

  public ResponseObject deactivateJobPost(long jobPostId) {
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    jobPost.get().setActive(false);
    jobPostRepository.save(jobPost.get());
    return new ResponseObject(200, "Deactivate success", null);
  }

  public void validateJobPost(JobPostDTO jobPostDTO) {
    // check budget
    if (jobPostDTO.getMinBudget() > jobPostDTO.getMaxBudget()) {
      throw new CustomException("Min budget can't be greater than max budget");
    }

    if ( jobPostDTO.getDueTime()!=null && jobPostDTO.getDueTime().before(new Date())) {
      throw new CustomException("Due time invalid");
    }
  }

  public ResponseObject getJobPostDueSoon() {
    String strQuery = "SELECT jp FROM JobPostEntity jp ORDER BY jp.dueTime ASC";
    CustomNullsFirstInterceptor firstInterceptor = new CustomNullsFirstInterceptor();
    strQuery = firstInterceptor.onPrepareStatement(strQuery);
    List<JobPostEntity> jobPostEntities =
        em.createQuery(strQuery).setMaxResults(15).getResultList();

    List<JobPostDTO> jobPostDTO0s =
        jobPostEntities.stream()
            .map(jobPost -> converter.toDTO(jobPost))
            .collect(Collectors.toList());
    return new ResponseObject(200, "Job Posts due soon", jobPostDTO0s);
  }

  public ResponseObject getHotJobPost() {
    String strQuery = "SELECT jp FROM JobPostEntity jp ORDER BY jp.createTime DESC";
    CustomNullsFirstInterceptor firstInterceptor = new CustomNullsFirstInterceptor();
    strQuery = firstInterceptor.onPrepareStatement(strQuery);
    // TypedQuery q = em.createQuery(strQuery, JobPostEntity.class);

    List<JobPostEntity> jobPostEntities =
        em.createQuery(strQuery).setMaxResults(15).getResultList();

    List<JobPostDTO> jobPostDTO0s =
        jobPostEntities.stream()
            .map(jobPost -> converter.toDTO(jobPost))
            .collect(Collectors.toList());
    return new ResponseObject(200, " Hot Job Posts", jobPostDTO0s);
  }

  @Override
  public ResponseObject getJobPostAmount() {
    long amount = jobPostRepository.count();

    return new ResponseObject(200, "Job Post amount", amount);
  }

  @Override
  public DataResponse getViewedJobPostAmountByUserId(long userId) {
    long amount = jobPostRepository.getViewedJobPostAmountByUser(userId);

    return new DataResponse(amount);
  }

  @Override
  public DataResponse countJobPostViewReturnDataResponse(long jobPostId) {
    return new DataResponse("Current view: " + countJobPostView(jobPostId));
  }

  public long countJobPostView(long jobPostId) {
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    if (jobPost.isEmpty()) {
      throw new CustomException("Cannot find job post with id = " + jobPostId);
    }
    jobPost.get().setViews(jobPost.get().getViews() + 1);
    jobPostRepository.save(jobPost.get());
    return jobPost.get().getViews();
  }

  @Override
  public DataResponse viewJobPost(long userId, long jobPostId) {
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    if (jobPost.isEmpty()) {
      throw new CustomException("Cannot find job post with id = " + jobPostId);
    }

    Optional<UserEntity> userEntity = userRepository.findById(userId);
    if (userEntity.isEmpty()) {
      throw new CustomException("Not found user");
    }

    jobPost.get().getViewedUsers().add(userEntity.get());
    jobPostRepository.save(jobPost.get());

    return new DataResponse(null);
  }

  @Override
  public DataResponse getApplicationRateByJobPostId(long jobPostId) {
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    if (jobPost.isEmpty()) {
      throw new CustomException("Cannot find job post with id = " + jobPostId);
    }

    double rate = 0; // mac dinh, hoi sai neu view = 0 và application > 0
    long viewOfCandidateAmount = jobPostRepository.getViewedCandidateAmountByJobPostId(jobPostId);
    long applicationOfCandidateAmount =
        applicationRepository.getAmountApplicationByJobPostId(jobPostId);

    if (viewOfCandidateAmount != 0 && viewOfCandidateAmount >= applicationOfCandidateAmount) {
      rate = Double.valueOf(applicationOfCandidateAmount) / viewOfCandidateAmount;
      rate = rate * 100; // doi ti le ra phan tram
      // lam tron 2 chu so thap phan
      rate = Math.round(rate * 100.0) / 100.0;
    }

    return new DataResponse(rate);
  }

  public long getLimitNumberOfJobPostsCreatedForEmployer(long employerId) {
    long limitNum = 0;
    // check subscribes of employer
    String sql =
        "SELECT SUM(pack.numOfJobPost) FROM SubscribeEntity sub, PackageEntity pack, UserEntity u WHERE sub.packageEntity.id = pack.id "
            + "AND sub.regUser.id = u.id AND u.id=:userId AND sub.expirationTime > :nowTime ";
    try {
      limitNum =
          (long)
              em.createQuery(sql)
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
    String sql =
        "SELECT COUNT(jp) FROM JobPostEntity jp WHERE jp.createdEmployer.id = :employerId AND jp.isDeleted = FALSE";
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
          "Your current number of created jobs is"
              + current
              + ", which has exceeded the limit of "
              + limit);
    }
  }

  public static void processListJobPost(Collection<JobPostDTO> jobPostDTOS){
    jobPostDTOS.forEach(jobPostDTO -> {
      jobPostDTO.setDescription(null);
    });
  }
}
