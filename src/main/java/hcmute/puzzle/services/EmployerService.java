package hcmute.puzzle.services;

import com.detectlanguage.errors.APIError;
import hcmute.puzzle.hirize.model.AIMatcherData;
import hcmute.puzzle.hirize.model.HirizeResponse;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface EmployerService {
  EmployerDto save(EmployerDto employerDTO);

  void delete(long id);

  EmployerDto update(EmployerDto employerDTO);

  EmployerDto getOne(long id);

  List<EmployerDto> getEmployerFollowedByCandidateId(long candidateId);

  double getApplicationRateEmployerId(long employerId);

  HirizeResponse<AIMatcherData> getPointOfApplicationFromHirize(Long jobPostId, Long candidateId) throws IOException,
          APIError;
}
