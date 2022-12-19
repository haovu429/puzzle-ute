package hcmute.puzzle.services;

import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.response.DataResponse;

public interface JobPostService {
  ResponseObject add(JobPostDTO jobPostDTO);

  ResponseObject delete(long id);

  DataResponse markJobPostWasDelete(long id);

  ResponseObject update(JobPostDTO jobPostDTO);

  ResponseObject getOne(long id);

  void validateJobPost(JobPostDTO jobPostDTO);

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

  DataResponse viewJobPost(long userId, long jobPostId);

  DataResponse getApplicationRateByJobPostId(long jobPostId);

  long getLimitNumberOfJobPostsCreatedForEmployer(long employerId);
}
