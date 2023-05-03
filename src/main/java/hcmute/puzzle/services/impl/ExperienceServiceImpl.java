package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.ExperienceDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.CandidateEntity;
import hcmute.puzzle.infrastructure.entities.ExperienceEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.ExperienceRepository;
import hcmute.puzzle.services.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ExperienceServiceImpl implements ExperienceService {

  @Autowired Converter converter;

  @Autowired ExperienceRepository experienceRepository;

  @Autowired CandidateRepository candidateRepository;

  @Override
  public ResponseObject save(long candidateId, ExperienceDto experienceDTO) {
    experienceDTO.setId(0);

    ExperienceEntity experience = converter.toEntity(experienceDTO);

    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);

    experience.setCandidateEntity(candidate.get());

    experience = experienceRepository.save(experience);

    return new ResponseObject(200, "Save success", converter.toDTO(experience));
  }

  @Override
  public ResponseObject update(ExperienceDto experienceDTO) {
    boolean exists = experienceRepository.existsById(experienceDTO.getId());

    if (!exists) {
      throw new CustomException("Experience isn't exists");
    }

    ExperienceEntity experience = converter.toEntity(experienceDTO);
    experience = experienceRepository.save(experience);
    return new ResponseObject(200, "Update successfully", converter.toDTO((experience)));
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = experienceRepository.existsById(id);

    if (!exists) {
      throw new CustomException("Experience isn't exists");
    }

    experienceRepository.deleteById(id);

    return new ResponseObject(200, "Delete successfully", null);
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
  public ResponseObject getAllExperienceByCandidateId(long experienceId) {
    Set<ExperienceDto> experienceDtos = new HashSet<>();
    experienceRepository.findAllByCandidateEntity_Id(experienceId).stream()
        .forEach(
            experience -> {
              experienceDtos.add(converter.toDTO(experience));
            });
    return new ResponseObject(200, "Info experience by candidate", experienceDtos);
  }

  @Override
  public ResponseObject getOneById(long id) {
    Optional<ExperienceEntity> experience = experienceRepository.findById(id);
    return new ResponseObject(200, "Info experience", converter.toDTO(experience.get()));
  }
}
