package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.ExperienceDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;

import java.util.List;

public interface ExperienceService {

  ExperienceDto save(long candidateId, ExperienceDto experienceDTO);

  ExperienceDto update(ExperienceDto experienceDTO);

  void delete(long id);

  ResponseObject getAll();

  List<ExperienceDto> getAllExperienceByCandidateId(long experienceId);

  ExperienceDto getOneById(long id);
}
