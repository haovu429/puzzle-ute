package hcmute.puzzle.services;

import hcmute.puzzle.dto.ExperienceDTO;
import hcmute.puzzle.dto.ResponseObject;

public interface ExperienceService {

  ResponseObject save(long candidateId, ExperienceDTO experienceDTO);

  ResponseObject update(ExperienceDTO experienceDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllExperienceByCandidateId(long experienceId);

  ResponseObject getOneById(long id);
}
