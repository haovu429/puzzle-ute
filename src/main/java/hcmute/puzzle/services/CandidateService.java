package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.PostCandidateRequest;

import java.util.Optional;

public interface CandidateService {

  CandidateDto save(CandidateDto candidateDTO);

  void delete(long id);

  CandidateDto update(long id, PostCandidateRequest postCandidateRequest);

  CandidateDto getOne(long id);

  void followEmployer(long candidateId, long employerId);

  void cancelFollowedEmployer(long candidateId, long employerId);

  void followCompany(long candidateId, long companyId);

  void cancelFollowedCompany(long candidateId, long companyId);

  void saveJobPost(long candidateId, long companyId);

  void cancelSavedJobPost(long candidateId, long jobPostId);
}
