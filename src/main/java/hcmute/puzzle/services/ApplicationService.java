package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.response.DataResponse;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
  ResponseObject findById(Long id);

  ResponseObject deleteById(Long id);

  ResponseObject findAll(Pageable pageable);

  ResponseObject applyJobPost(long candidateId, long jobPostId);

  ResponseObject responseApplication(long applicationId, boolean isAccept, String note);

  ResponseObject getApplicationByJobPostId(long jobPostId);

  ResponseObject getApplicationByJobPostIdAndCandidateId(long jobPostId, long candidateId);

  ResponseObject responseApplicationByCandidateAndJobPost(long candidateId, long joPostId, boolean isAccept, String note);

  ResponseObject getApplicationAmount();

  DataResponse getAmountApplicationToEmployer(long employerId);

  DataResponse getAmountApplicationByJobPostId(long jobPostId);

  DataResponse getCandidateAppliedToJobPostIdAndResult(long jobPostId);

  DataResponse getCandidateAppliedToEmployerAndResult(long employerId);
}
