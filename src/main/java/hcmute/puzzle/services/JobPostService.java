package hcmute.puzzle.services;

import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;

public interface JobPostService {
  ResponseObject add(JobPostDTO jobPostDTO);

  ResponseObject delete(long id);

  ResponseObject update(JobPostDTO jobPostDTO);

  ResponseObject getOne(long id);

  void validateJobPost(JobPostDTO jobPostDTO);

  ResponseObject getAll();

  ResponseObject getCandidatesApplyJobPost(long jobPostId);

  ResponseObject getJobPostAppliedByCandidateId(long candidateId);

  ResponseObject getJobPostCreatedByEmployerId(long employerId);

  ResponseObject getJobPostSavedByCandidateId(long candidateId);

  ResponseObject activateJobPost(long jobPostId);

  ResponseObject deactivateJobPost(long jobPostId);
}
