package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ExperienceDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.ExperienceEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.ExperienceRepository;
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
  public ResponseObject save(long candidateId, ExperienceDTO experienceDTO) {
    experienceDTO.setId(0);

    ExperienceEntity experience = converter.toEntity(experienceDTO);

    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);

    experience.setCandidateEntity(candidate.get());

    experience = experienceRepository.save(experience);

    return new ResponseObject(200, "Save success", converter.toDTO(experience));
  }

  @Override
  public ResponseObject update(ExperienceDTO experienceDTO) {
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
    Set<ExperienceDTO> experienceDTOS = new HashSet<>();
    experienceRepository.findAll().stream()
        .forEach(
            experience -> {
              experienceDTOS.add(converter.toDTO(experience));
            });

    return new ResponseObject(200, "Info Experience", experienceDTOS);
  }

  @Override
  public ResponseObject getAllExperienceByCandidateId(long experienceId) {
    Set<ExperienceDTO> experienceDTOS = new HashSet<>();
    experienceRepository.findAllByCandidateEntity_Id(experienceId).stream()
        .forEach(
            experience -> {
              experienceDTOS.add(converter.toDTO(experience));
            });
    return new ResponseObject(200, "Info experience by candidate", experienceDTOS);
  }

  @Override
  public ResponseObject getOneById(long id) {
    Optional<ExperienceEntity> experience = experienceRepository.findById(id);
    return new ResponseObject(200, "Info experience", converter.toDTO(experience.get()));
  }
}
