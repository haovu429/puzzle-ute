package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.models.ApplicationResult;
import hcmute.puzzle.infrastructure.models.CandidateAppliedAndResult;
import hcmute.puzzle.infrastructure.models.ResponseApplication;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
  ApplicationDto findById(Long id);

  void deleteById(Long id);

  Page<ApplicationDto> findAll(Pageable pageable);

  void applyJobPost(long candidateId, long jobPostId);

  void responseApplication(long applicationId, ApplicationResult applicationResult);

  Page<ApplicationDto> getApplicationByJobPostId(long jobPostId, Pageable pageable);

  ResponseObject getApplicationByJobPostIdAndCandidateId(long jobPostId, long candidateId);

  void responseApplicationByCandidateAndJobPost(ResponseApplication responseApplication);

  ResponseObject getApplicationAmount();

  DataResponse getAmountApplicationToEmployer(long employerId);

  DataResponse getAmountApplicationByJobPostId(long jobPostId);

  Page<CandidateAppliedAndResult> getCandidateAppliedToJobPostIdAndResult(long jobPostId, Pageable pageable);

  DataResponse getCandidateAppliedToEmployerAndResult(long employerId);
}
