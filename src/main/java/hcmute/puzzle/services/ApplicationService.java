package hcmute.puzzle.services;

import hcmute.puzzle.exception.InvalidBehaviorException;
import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.ApplicationRequest;
import hcmute.puzzle.infrastructure.dtos.response.CandidateApplicationResult;
import hcmute.puzzle.infrastructure.models.ApplicationResult;
import hcmute.puzzle.infrastructure.models.CandidateAppliedAndResult;
import hcmute.puzzle.infrastructure.models.ResponseApplication;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
  ApplicationDto findById(Long id);

  void deleteById(Long id);

  Page<ApplicationDto> findAll(Pageable pageable);

  ApplicationDto applyJobPost(long candidateId, long jobPostId);

  ApplicationDto responseApplication(long applicationId, ApplicationResult applicationResult);

  Page<ApplicationDto> getApplicationByJobPostId(long jobPostId, Pageable pageable);

  ApplicationDto getApplicationByJobPostIdAndCandidateId(long jobPostId, long candidateId);

  ApplicationDto responseApplicationByCandidateAndJobPost(ResponseApplication responseApplication);

  Long getApplicationAmount();

  long getAmountApplicationToEmployer(long employerId);

  long getAmountApplicationByJobPostId(long jobPostId);

  Page<CandidateApplicationResult> getCandidateAppliedToJobPostIdAndResult(long jobPostId, Pageable pageable);

  List<CandidateApplicationResult> getCandidateAppliedToEmployerAndResult(long employerId);

  ApplicationDto candidateApply(long jobPostId, ApplicationRequest applicationRequest,
          MultipartFile cvFile) throws InvalidBehaviorException;
}
