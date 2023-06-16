package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.JobAlertDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.JobAlert;
import hcmute.puzzle.infrastructure.mappers.JobAlertMapper;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.JobAlertRepository;
import hcmute.puzzle.services.JobAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobAlertServiceImpl implements JobAlertService {

  @Autowired
  JobAlertRepository jobAlertRepository;

  @Autowired
  CandidateRepository candidateRepository;

  @Autowired
  Converter converter;

  @Autowired
  JobAlertMapper jobAlertMapper;

  @Override
  public JobAlertDto save(long candidateId, JobAlertDto jobAlertDTO) {
    jobAlertDTO.setId(0);

    JobAlert jobAlert = converter.toEntity(jobAlertDTO);

    Optional<Candidate> candidate = candidateRepository.findById(candidateId);

    jobAlert.setCandidate(candidate.get());

    jobAlert = jobAlertRepository.save(jobAlert);

    return jobAlertMapper.jobAlertToJobAlertDto(jobAlert);
  }

  @Override
  public JobAlertDto update(JobAlertDto jobAlertDTO) {
    boolean exists = jobAlertRepository.existsById(jobAlertDTO.getId());

    if (!exists) {
      throw new CustomException("Job Alert isn't exists");
    }

    JobAlert jobAlert = converter.toEntity(jobAlertDTO);
    jobAlert = jobAlertRepository.save(jobAlert);
    return jobAlertMapper.jobAlertToJobAlertDto(jobAlert);
  }

  @Override
  public void delete(long id) {
    boolean exists = jobAlertRepository.existsById(id);
    if (!exists) {
      throw new CustomException("Job Alert isn't exists");
    }
    jobAlertRepository.deleteById(id);
  }

  @Override
  public ResponseObject getAll() {
    Set<JobAlertDto> jobAlertDtos = new HashSet<>();
    jobAlertRepository.findAll().stream().forEach(jobAlert -> {
      jobAlertDtos.add(converter.toDTO(jobAlert));
    });

    return new ResponseObject<>(200, "Info JobAlert", jobAlertDtos);
  }

  public List<JobAlertDto> getAllJobAlertByCandidateId(long jobAlertId) {
    List<JobAlertDto> jobAlertDtos = new ArrayList<>();
    jobAlertDtos = jobAlertRepository.findAllByCandidate_Id(jobAlertId)
                                     .stream()
                                     .map(jobAlertMapper::jobAlertToJobAlertDto)
                                     .toList();
    return jobAlertDtos;
  }

  @Override
  public JobAlertDto getOneById(long id) {
    JobAlert jobAlert = jobAlertRepository.findById(id)
                                          .orElseThrow(() -> new NotFoundDataException("Not found job alert"));
    JobAlertDto jobAlertDto = jobAlertMapper.jobAlertToJobAlertDto(jobAlert);
    return jobAlertDto;
  }
}
