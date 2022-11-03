package hcmute.puzzle.services;

import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.ResponseObject;

import java.util.Optional;

public interface CandidateService {

  Optional<CandidateDTO> save(CandidateDTO candidateDTO);

  ResponseObject delete(long id);

  ResponseObject update(CandidateDTO candidateDTO);

  ResponseObject getOne(long id);

  ResponseObject followEmployer(long candidateId, long employerId);

  ResponseObject followCompany(long candidateId, long companyId);

  ResponseObject saveJobPost(long candidateId, long companyId);
}
