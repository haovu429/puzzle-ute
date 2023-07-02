package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.JobPostDtoOld;

import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.JobPostAdminPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.JobPostUserPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.RequestPageable;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.JobAlert;
import hcmute.puzzle.infrastructure.entities.JobPost;
import hcmute.puzzle.infrastructure.models.JobPostFilterRequest;
import hcmute.puzzle.infrastructure.models.JobPostWithApplicationAmount;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobPostService {
  JobPostDto add(JobPostUserPostRequest createJobPostRequest);

  Page<JobPostDto> filterJobPostByJobAlert(JobAlert jobAlert, Pageable pageable);

  void delete(long id);

  void markJobPostWasDelete(long id);

  JobPostDto updateJobPostWithRoleUser(long jobPostId, JobPostUserPostRequest jobPostUserPostRequest);

  JobPostDto updateJobPostWithRoleAdmin(long jobPostId, JobPostAdminPostRequest jobPostAdminPostRequest);
  JobPostDto getOne(long id);

  void validateJobPost(JobPostUserPostRequest jobPostUserPostRequest);

  Page<JobPostDto> getAll(Pageable pageable);

  List<CandidateDto> getCandidatesApplyJobPost(long jobPostId);

  List<JobPostDto> getJobPostAppliedByCandidateId(long candidateId);

  Page<JobPostWithApplicationAmount> getJobPostCreatedByEmployerId(long employerId, Pageable pageable);

  List<JobPostDto> getJobPostSavedByCandidateId(long candidateId);

  List<JobPostDto> getActiveJobPost();

  List<JobPostDto> getInactiveJobPost();


  List<JobPostDto> getJobPostByCreateEmployerId(long employerId, boolean isActive);

  void activateJobPost(long jobPostId);

  void deactivateJobPost(long jobPostId);

  List<JobPostDto> getJobPostDueSoon();

  List<JobPostDto> getHotJobPost();

  long getJobPostAmount();

  long getViewedJobPostAmountByUserId(long userId);

  long countJobPostViewReturnDataResponse(long jobPostId);

  long countJobPostView(long jobPostId);

  void viewJobPost(long userId, long jobPostId);

  double getApplicationRateByJobPostId(long jobPostId);

  long getLimitNumberOfJobPostsCreatedForEmployer(long employerId);

  long getTotalJobPostViewOfEmployer(long employerId);

  void checkCreatedJobPostLimit(long employerId);

  Page<JobPostDto> filterJobPost(JobPostFilterRequest jobPostFilterRequest, Pageable pageable);
}
