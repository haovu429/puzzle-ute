package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.services.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobPostServiceImpl implements JobPostService {

  @Autowired Converter converter;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired CandidateRepository candidateRepository;

  public ResponseObject add(JobPostDTO jobPostDTO) {

    JobPostEntity jobPostEntity = converter.toEntity(jobPostDTO);

    // set id
    jobPostEntity.setId(0);

    jobPostEntity = jobPostRepository.save(jobPostEntity);

    return new ResponseObject(200, "Save job post Successfully", converter.toDTO(jobPostEntity));
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = jobPostRepository.existsById(id);
    if (exists) {
      jobPostRepository.deleteById(id);
      return new ResponseObject(200, "Delete job post Successfully", null);
    }
    throw new CustomException("Cannot find job post with id =" + id);
  }

  @Override
  public ResponseObject update(JobPostDTO JobPostDTO) {

    boolean exists = jobPostRepository.existsById(JobPostDTO.getId());

    if (exists) {
      JobPostEntity candidate = converter.toEntity(JobPostDTO);
      // candidate.setId(candidate.getUserEntity().getId());

      jobPostRepository.save(candidate);
      return new ResponseObject(converter.toDTO(candidate));
    }

    throw new CustomException("Cannot find job post with id = " + JobPostDTO.getId());
  }

  @Override
  public ResponseObject getOne(long id) {
    boolean exists = jobPostRepository.existsById(id);

    if (exists) {
      JobPostEntity candidate = jobPostRepository.getReferenceById(id);
      return new ResponseObject(200, "Info of job post", converter.toDTO(candidate));
    }

    throw new CustomException("Cannot find job post with id = " + id);
  }

  @Override
  public ResponseObject getAll() {
    List<JobPostEntity> jobPostEntities = new ArrayList<>();

    jobPostEntities = jobPostRepository.findAll();

    List<JobPostDTO> jobPostDTOS =
        jobPostEntities.stream()
            .map(
                entity -> {
                  return converter.toDTO(entity);
                })
            .collect(Collectors.toList());

    return new ResponseObject(200, "Info of job post", jobPostDTOS);
  }

  public ResponseObject getCandidatesApplyJobPost(long jobPostId) {
    Set<CandidateEntity> candidateApply = jobPostRepository.getCandidateApplyJobPost(jobPostId);
    Set<CandidateDTO> candidateDTOS =
        candidateApply.stream()
            .map(candidate -> converter.toDTO(candidate))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Candidate applied", candidateDTOS);
  }

  @Override
  public ResponseObject getJobPostAppliedByCandidateId(long candidateId) {
    Set<JobPostDTO> jobPostDTOS =
        jobPostRepository.findAllByAppliedCandidateId(candidateId).stream()
            .map(jobPostEntity -> converter.toDTO(jobPostEntity))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Job Post applied", jobPostDTOS);
  }

  @Override
  public ResponseObject getJobPostSavedByCandidateId(long candidateId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    Set<JobPostDTO> jobPostDTOS =
        candidate.get().getSavedJobPost().stream()
            .map(jobPost -> converter.toDTO(jobPost))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Job Post applied", jobPostDTOS);
  }

  public ResponseObject activateJobPost(long jobPostId) {
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    jobPost.get().setActive(true);
    jobPostRepository.save(jobPost.get());
    return new ResponseObject(200, "Activate success", null);
  }

  public ResponseObject deactivateJobPost(long jobPostId) {
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    jobPost.get().setActive(false);
    jobPostRepository.save(jobPost.get());
    return new ResponseObject(200, "Deactivate success", null);
  }

  public void validateJobPost(JobPostDTO jobPostDTO) {
    // check budget
    if (jobPostDTO.getMinBudget() > jobPostDTO.getMaxBudget()) {
      throw new CustomException("Min budget can't be greater than max budget");
    }
  }
}
