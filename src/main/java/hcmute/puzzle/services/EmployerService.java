package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;

import java.util.Optional;

public interface EmployerService {
  Optional<EmployerDto> save(EmployerDto employerDTO);

  ResponseObject delete(long id);

  ResponseObject update(EmployerDto employerDTO);

  ResponseObject getOne(long id);

  ResponseObject getEmployerFollowedByCandidateId(long candidateId);

  DataResponse getApplicationRateEmployerId(long employerId);
}
