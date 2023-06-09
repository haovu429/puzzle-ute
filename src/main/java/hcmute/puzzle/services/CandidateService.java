package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.PostCandidateRequest;

import java.util.Optional;

public interface CandidateService {

  CandidateDto save(CandidateDto candidateDTO);

  ResponseObject delete(long id);

  CandidateDto update(long id, PostCandidateRequest postCandidateRequest);

  ResponseObject getOne(long id);

  ResponseObject followEmployer(long candidateId, long employerId);

  ResponseObject cancelFollowedEmployer(long candidateId, long employerId);

  ResponseObject followCompany(long candidateId, long companyId);

  ResponseObject cancelFollowedCompany(long candidateId, long companyId);

  ResponseObject saveJobPost(long candidateId, long companyId);

  ResponseObject cancelSavedJobPost(long candidateId, long jobPostId);
}
