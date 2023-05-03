package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.JobPostDto;

import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

public interface JobPostService {
  ResponseObject add(JobPostDto jobPostDTO);

  ResponseObject delete(long id);

  DataResponse markJobPostWasDelete(long id);

  ResponseObject update(JobPostDto jobPostDTO);

  ResponseObject getOne(long id);

  void validateJobPost(JobPostDto jobPostDTO);

  ResponseObject getAll();
  ResponseObject getJobPostWithPage(int pageNum, int numOfRecord);

  ResponseObject getCandidatesApplyJobPost(long jobPostId);

  ResponseObject getJobPostAppliedByCandidateId(long candidateId);

  ResponseObject getJobPostCreatedByEmployerId(long employerId);

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
}
