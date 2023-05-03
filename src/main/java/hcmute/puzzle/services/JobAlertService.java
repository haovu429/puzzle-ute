package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.JobAlertDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;

public interface JobAlertService {
  ResponseObject save(long candidateId, JobAlertDto jobAlertDTO);

  ResponseObject update(JobAlertDto jobAlertDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllJobAlertByCandidateId(long jobAlertId);

  ResponseObject getOneById(long id);
}
