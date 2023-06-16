package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;

import java.util.List;
import java.util.Optional;

public interface EmployerService {
  EmployerDto save(EmployerDto employerDTO);

  void delete(long id);

  EmployerDto update(EmployerDto employerDTO);

  EmployerDto getOne(long id);

  List<EmployerDto> getEmployerFollowedByCandidateId(long candidateId);

  double getApplicationRateEmployerId(long employerId);
}
