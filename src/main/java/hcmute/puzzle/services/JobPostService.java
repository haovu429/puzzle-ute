package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.JobPostDtoOld;

import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.RequestPageable;
import hcmute.puzzle.infrastructure.entities.JobPost;
import hcmute.puzzle.infrastructure.models.JobPostFilterRequest;
import hcmute.puzzle.infrastructure.models.JobPostWithApplicationAmount;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostService {
  ResponseObject add(JobPostDtoOld jobPostDTO);

  ResponseObject delete(long id);

  DataResponse markJobPostWasDelete(long id);

  ResponseObject update(JobPostDtoOld jobPostDTO);

  ResponseObject getOne(long id);

  void validateJobPost(JobPostDtoOld jobPostDTO);

  ResponseObject getAll();
  ResponseObject getJobPostWithPage(int pageNum, int numOfRecord);

  ResponseObject getCandidatesApplyJobPost(long jobPostId);

  ResponseObject getJobPostAppliedByCandidateId(long candidateId);

  Page<JobPostWithApplicationAmount> getJobPostCreatedByEmployerId(long employerId, Pageable pageable);

  ResponseObject getJobPostSavedByCandidateId(long candidateId);

  ResponseObject getActiveJobPost();

  ResponseObject getInactiveJobPost();

  ResponseObject getActiveJobPostByCreateEmployerId(long employerId);

  ResponseObject getInactiveJobPostByCreateEmployerId(long employerId);

  ResponseObject activateJobPost(long jobPostId);

  ResponseObject deactivateJobPost(long jobPostId);

  ResponseObject getJobPostDueSoon();

  ResponseObject getHotJobPost();

  ResponseObject getJobPostAmount();

  DataResponse getViewedJobPostAmountByUserId(long userId);

  DataResponse countJobPostViewReturnDataResponse(long jobPostId);

  long countJobPostView(long jobPostId);

  DataResponse viewJobPost(long userId, long jobPostId);

  DataResponse getApplicationRateByJobPostId(long jobPostId);

  long getLimitNumberOfJobPostsCreatedForEmployer(long employerId);

  long getTotalJobPostViewOfEmployer(long employerId);

  void checkCreatedJobPostLimit(long employerId);

  Page<JobPost> filterJobPost(RequestPageable<JobPostFilterRequest> jobPostFilterRequest);
}
