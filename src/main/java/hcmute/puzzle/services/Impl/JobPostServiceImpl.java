package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.services.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobPostServiceImpl implements JobPostService {

  @Autowired Converter converter;

  @Autowired JobPostRepository jobPostRepository;

  public Optional<JobPostDTO> add(JobPostDTO jobPostDTO) {

    JobPostEntity jobPostEntity = converter.toEntity(jobPostDTO);

    // set id
    jobPostEntity.setId(0);

    jobPostRepository.save(jobPostEntity);

    Optional<JobPostDTO> result = Optional.of(converter.toDTO(jobPostEntity));

    // return add Province success
    return result;
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

    List<JobPostDTO> jobPostDTOS = jobPostEntities.stream().map(entity -> {
      return converter.toDTO(entity);
    }).collect(Collectors.toList());

    return new ResponseObject(200, "Info of job post", jobPostDTOS);
  }

  public void validateJobPost(JobPostDTO jobPostDTO) {
    // check budget
    if (jobPostDTO.getMinBudget() > jobPostDTO.getMaxBudget()) {
      throw new CustomException("Min budget can't be greater than max budget");
    }
  }
}
