package hcmute.puzzle.services;

import hcmute.puzzle.dto.JobAlertDTO;
import hcmute.puzzle.dto.ResponseObject;

public interface JobAlertService {
  ResponseObject save(long candidateId, JobAlertDTO jobAlertDTO);

  ResponseObject update(JobAlertDTO jobAlertDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllJobAlertByCandidateId(long jobAlertId);

  ResponseObject getOneById(long id);
}
