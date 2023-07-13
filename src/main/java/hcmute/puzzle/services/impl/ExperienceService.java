package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.dtos.olds.ExperienceDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.CreateExperienceRequest;
import hcmute.puzzle.infrastructure.dtos.request.UpdateExperienceRequest;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.Experience;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.ExperienceMapper;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ExperienceService {

  @Autowired
  ExperienceRepository experienceRepository;

  @Autowired
  CandidateRepository candidateRepository;

  @Autowired
  ExperienceMapper experienceMapper;

  @Autowired
  CurrentUserService currentUserService;


  public ExperienceDto save(long candidateId, CreateExperienceRequest createExperienceRequest) {
    Experience experience = experienceMapper.createExperienceRequestToExperience(createExperienceRequest);
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(() -> new NotFoundDataException("Not found candidate"));
    experience.setCandidate(candidate);
    experienceRepository.save(experience);
    return experienceMapper.experienceToExperienceDto(experience);
  }


  public ExperienceDto update(long experienceId, UpdateExperienceRequest updateExperienceRequest) {
    User currentUser = currentUserService.getCurrentUser();
    Experience experience = experienceRepository.findById(experienceId)
                                                .orElseThrow(() -> new NotFoundDataException("Not found experience"));
    Candidate candidate = candidateRepository.findByExperienceId(experience.getId())
                                             .orElseThrow(() -> new NotFoundDataException("Not found candidate"));
    if (candidate.getId() != currentUser.getId()) {
      throw new UnauthorizedException("You don't have rights for this Experience");
    }
    experienceMapper.updateExperienceFromUpdateExperienceRequest(updateExperienceRequest, experience);
    experienceRepository.save(experience);
    return experienceMapper.experienceToExperienceDto(experience);
  }


  public void delete(long experienceId) {
    User currentUser = currentUserService.getCurrentUser();
    Experience experience = experienceRepository.findById(experienceId)
                                                .orElseThrow(() -> new NotFoundDataException("Not found experience"));
    Candidate candidate = candidateRepository.findByExperienceId(experience.getId())
                                             .orElseThrow(() -> new NotFoundDataException("Not found candidate"));
    if (candidate.getId() != currentUser.getId()) {
      throw new UnauthorizedException("You don't have rights for this Experience");
    }
    experienceRepository.delete(experience);
  }


  public ResponseObject getAll() {
    Set<ExperienceDto> experienceDtos = new HashSet<>();
    experienceRepository.findAll().stream().forEach(experience -> {
      experienceDtos.add(experienceMapper.experienceToExperienceDto(experience));
            });

    return new ResponseObject(200, "Info Experience", experienceDtos);
  }


  public List<ExperienceDto> getAllExperienceByCandidateId(long experienceId) {
    List<ExperienceDto> experienceDtos = new ArrayList<>();
    experienceRepository.findAllByCandidate_Id(experienceId)
						.forEach(
            experience -> {
              experienceDtos.add(experienceMapper.experienceToExperienceDto(experience));
            });
    return experienceDtos;
  }


  public ExperienceDto getOneById(long id) {
    Experience experience = experienceRepository.findById(id)
                                                .orElseThrow(() -> new NotFoundDataException("Not found experience"));
    return experienceMapper.experienceToExperienceDto(experience);
  }
}
