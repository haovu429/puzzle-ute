package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.JobAlertDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.CandidateEntity;
import hcmute.puzzle.infrastructure.entities.JobAlertEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.JobAlertRepository;
import hcmute.puzzle.services.JobAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class JobAlertServiceImpl implements JobAlertService {

  @Autowired JobAlertRepository jobAlertRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired Converter converter;

  @Override
  public ResponseObject save(long candidateId, JobAlertDto jobAlertDTO) {
    jobAlertDTO.setId(0);

    JobAlertEntity jobAlert = converter.toEntity(jobAlertDTO);

    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);

    jobAlert.setCandidateEntity(candidate.get());

    jobAlert = jobAlertRepository.save(jobAlert);

    return new ResponseObject(200, "Save success", converter.toDTO(jobAlert));
  }

  @Override
  public ResponseObject update(JobAlertDto jobAlertDTO) {
    boolean exists = jobAlertRepository.existsById(jobAlertDTO.getId());

    if (!exists) {
      throw new CustomException("Job Alert isn't exists");
    }

    JobAlertEntity jobAlert = converter.toEntity(jobAlertDTO);
    jobAlert = jobAlertRepository.save(jobAlert);
    return new ResponseObject(200, "Update successfully", converter.toDTO((jobAlert)));
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = jobAlertRepository.existsById(id);

    if (!exists) {
      throw new CustomException("Job Alert isn't exists");
    }

    jobAlertRepository.deleteById(id);

    return new ResponseObject(200, "Delete successfully", null);
  }

  @Override
  public ResponseObject getAll() {
    Set<JobAlertDto> jobAlertDtos = new HashSet<>();
    jobAlertRepository.findAll().stream()
        .forEach(
            jobAlert -> {
              jobAlertDtos.add(converter.toDTO(jobAlert));
            });

    return new ResponseObject(200, "Info JobAlert", jobAlertDtos);
  }

  public ResponseObject getAllJobAlertByCandidateId(long jobAlertId) {
    Set<JobAlertDto> jobAlertDtos = new HashSet<>();
    jobAlertRepository.findAllByCandidateEntity_Id(jobAlertId).stream()
        .forEach(
            jobAlert -> {
              jobAlertDtos.add(converter.toDTO(jobAlert));
            });
    return new ResponseObject(200, "Info JobAlert by candidate", jobAlertDtos);
  }

  @Override
  public ResponseObject getOneById(long id) {
    Optional<JobAlertEntity> jobAlert = jobAlertRepository.findById(id);
    return new ResponseObject(200, "Info job alert", converter.toDTO(jobAlert.get()));
  }
}
