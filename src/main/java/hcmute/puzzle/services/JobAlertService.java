package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.JobAlertDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;

import java.util.List;

public interface JobAlertService {
  JobAlertDto save(long candidateId, JobAlertDto jobAlertDTO);

  JobAlertDto update(JobAlertDto jobAlertDTO);

  void delete(long id);

  ResponseObject getAll();

  List<JobAlertDto> getAllJobAlertByCandidateId(long jobAlertId);

  JobAlertDto getOneById(long id);
}
