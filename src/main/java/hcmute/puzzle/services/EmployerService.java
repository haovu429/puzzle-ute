package hcmute.puzzle.services;

import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.response.DataResponse;

import java.util.Optional;

public interface EmployerService {
  Optional<EmployerDTO> save(EmployerDTO employerDTO);

  ResponseObject delete(long id);

  ResponseObject update(EmployerDTO employerDTO);

  ResponseObject getOne(long id);

  ResponseObject getEmployerFollowedByCandidateId(long candidateId);

  DataResponse getApplicationRateEmployerId(long employerId);
}
