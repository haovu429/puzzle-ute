package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.ExperienceDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;

public interface ExperienceService {

  ResponseObject save(long candidateId, ExperienceDto experienceDTO);

  ResponseObject update(ExperienceDto experienceDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllExperienceByCandidateId(long experienceId);

  ResponseObject getOneById(long id);
}
