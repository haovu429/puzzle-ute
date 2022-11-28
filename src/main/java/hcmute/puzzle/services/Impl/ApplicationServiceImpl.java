package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ApplicationDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.ApplicationEntity;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {
  @Autowired ApplicationRepository applicationRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired JobPostRepository jobPostRepository;
  @Autowired Converter converter;

  @Override
  public ResponseObject findById(Long id) {
    Optional<ApplicationEntity> dto = applicationRepository.findById(id);
    if (!dto.isPresent()) {
      throw new CustomException("Can not found Address with id " + id);
    }
    return new ResponseObject(converter.toDTO(dto.get()));
  }

  @Override
  public ResponseObject deleteById(Long id) {
    Optional<ApplicationEntity> foundEntity = applicationRepository.findById(id);
    if (!foundEntity.isPresent()) {
      throw new RuntimeException("Can not found Application with id " + id);
    }
    applicationRepository.delete(foundEntity.get());
    return new ResponseObject(200, " successful delete application with id " + id, null);
  }

  @Override
  public ResponseObject findAll(Pageable pageable) {

    Page<ApplicationEntity> entities = applicationRepository.findAll(pageable);
    Page<ApplicationDTO> dtos =
        entities.map(
            new Function<ApplicationEntity, ApplicationDTO>() {
              @Override
              public ApplicationDTO apply(ApplicationEntity entity) {
                return converter.toDTO(entity);
              }
            });
    return new ResponseObject(dtos);
  }

  @Override
  // Candidate apply jobPost
  public ResponseObject applyJobPost(long candidateId, long jobPostId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    ApplicationEntity application = new ApplicationEntity();

    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (jobPost.isEmpty()) {
      throw new NoSuchElementException("Employer no value present");
    }

    application.setCandidateEntity(candidate.get());
    application.setJobPostEntity(jobPost.get());
    applicationRepository.save(application);

    return new ResponseObject(200, " Apply success! ", converter.toDTO(application));
  }

  @Override
  public ResponseObject responseApplication(long applicationId, boolean isAccept, String note) {
    Optional<ApplicationEntity> application = applicationRepository.findById(applicationId);
    if (isAccept) {
      application.get().setResult("ACCEPT");
    } else {
      application.get().setResult("REJECT");
    }
    application.get().setNote(note);
    applicationRepository.save(application.get());
    return new ResponseObject(200, "Response success", null);
  }

  public ResponseObject getApplicationByJobPostId(long jobPostId) {
    if (!jobPostRepository.existsById(jobPostId)) {
      throw new CustomException("Job Post isn't exists");
    }

    List<ApplicationDTO> applicationDTOS =
        applicationRepository.findApplicationByJobPostId(jobPostId).stream()
            .map(
                application -> {
                  ApplicationDTO applicationDTO = converter.toDTO(application);
                  Optional<CandidateEntity> candidate = candidateRepository.findById(applicationDTO.getCandidateId());
                  if (candidate.isEmpty()) {
                    throw new CustomException("Data candidate is wrong");
                  }

                  applicationDTO.setCandidateDTO(converter.toDTO(candidate.get()));
                  return applicationDTO;
                })
            .collect(Collectors.toList());
    return new ResponseObject(
        200, "List application for job post id = " + jobPostId, applicationDTOS);
  }
}
