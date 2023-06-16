package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.ExperienceDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.Experience;
import hcmute.puzzle.infrastructure.mappers.ExperienceMapper;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.ExperienceRepository;
import hcmute.puzzle.services.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExperienceServiceImpl implements ExperienceService {

  @Autowired
  Converter converter;

  @Autowired
  ExperienceRepository experienceRepository;

  @Autowired
  CandidateRepository candidateRepository;

  @Autowired
  ExperienceMapper experienceMapper;

  @Override
  public ExperienceDto save(long candidateId, ExperienceDto experienceDTO) {
    experienceDTO.setId(0);

    Experience experience = converter.toEntity(experienceDTO);

    Optional<Candidate> candidate = candidateRepository.findById(candidateId);

    experience.setCandidate(candidate.get());

    experience = experienceRepository.save(experience);

    return experienceMapper.evaluateToEvaluateDto(experience);
  }

  @Override
  public ExperienceDto update(ExperienceDto experienceDTO) {
    boolean exists = experienceRepository.existsById(experienceDTO.getId());

    if (!exists) {
      throw new CustomException("Experience isn't exists");
    }

    Experience experience = converter.toEntity(experienceDTO);
    experience = experienceRepository.save(experience);
    ExperienceDto experienceDto = experienceMapper.evaluateToEvaluateDto(experience);
    return experienceDto;
  }

  @Override
  public void delete(long id) {
    boolean exists = experienceRepository.existsById(id);
    if (!exists) {
      throw new CustomException("Experience isn't exists");
    }
    experienceRepository.deleteById(id);
  }

  @Override
  public ResponseObject getAll() {
    Set<ExperienceDto> experienceDtos = new HashSet<>();
    experienceRepository.findAll().stream()
        .forEach(
            experience -> {
              experienceDtos.add(converter.toDTO(experience));
            });

    return new ResponseObject(200, "Info Experience", experienceDtos);
  }

  @Override
  public List<ExperienceDto> getAllExperienceByCandidateId(long experienceId) {
    List<ExperienceDto> experienceDtos = new ArrayList<>();
    experienceRepository.findAllByCandidate_Id(experienceId)
						.forEach(
            experience -> {
              experienceDtos.add(converter.toDTO(experience));
            });
    return experienceDtos;
  }

  @Override
  public ExperienceDto getOneById(long id) {
    Experience experience = experienceRepository.findById(id)
                                                .orElseThrow(() -> new NotFoundDataException("Not found experience"));
    return experienceMapper.evaluateToEvaluateDto(experience);
  }
}
