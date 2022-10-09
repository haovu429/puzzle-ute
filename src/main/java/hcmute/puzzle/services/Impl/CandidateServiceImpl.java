package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.EmployerEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.EmployerRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CandidateServiceImpl implements CandidateService {

  @Autowired CandidateRepository candidateRepository;

  @Autowired EmployerRepository employerRepository;

  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired Converter converter;

  @Override
  public Optional<CandidateDTO> save(CandidateDTO candidateDTO) {
    // casting provinceDTO to ProvinceEntity
    CandidateEntity candidateEntity = converter.toEntity(candidateDTO);

    // set id
    candidateEntity.setId(0);

    if (candidateEntity.getUserEntity().getEmployerEntity() != null) {
      throw new RuntimeException("This account for employer");
    }

    candidateRepository.save(candidateEntity);

    Optional<CandidateDTO> result = Optional.of(converter.toDTO(candidateEntity));

    // return add Province success
    return result;
  }

  public boolean checkProductCodeExists(String productCode, Optional<Long> updateId) {
    // allow updating with old product_code

    // Save
    if (updateId == null) {
      updateId = Optional.of(-1L);
    }
    // Update
    //        long count = candidateRepository.countByProductCodeWithUpdateId(productCode,
    // updateId.get());
    //        if (count > 0) {
    //            return true;
    //        }
    return false;
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = candidateRepository.existsById(id);
    if (exists) {
      candidateRepository.deleteById(id);
      return new ResponseObject(200, "Delete candidate Successfully", null);
    }
    throw new CustomException("Cannot find candidate with id =" + id);
  }

  @Override
  public ResponseObject update(CandidateDTO candidateDTO) {

    boolean exists = candidateRepository.existsById(candidateDTO.getId());

    if (exists) {
      CandidateEntity candidate = converter.toEntity(candidateDTO);
      // candidate.setId(candidate.getUserEntity().getId());

      candidateRepository.save(candidate);
      return new ResponseObject(converter.toDTO(candidate));
    }

    throw new CustomException("Cannot find candidate with id = " + candidateDTO.getId());
  }

  @Override
  public ResponseObject getOne(long id) {
    boolean exists = candidateRepository.existsById(id);

    if (exists) {
      CandidateEntity candidate = candidateRepository.getReferenceById(id);

      return new ResponseObject(200, "Info of candidate", converter.toDTO(candidate));
    }

    throw new CustomException("Cannot find candidate with id = " + id);
  }

  @Override
  public ResponseObject followEmployer(long candidateId, long employerId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<EmployerEntity> employer = employerRepository.findById(employerId);
    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (employer.isEmpty()) {
      throw new NoSuchElementException("Employer no value present");
    }

    candidate.get().getFollowingEmployers().add(employer.get());
    candidateRepository.save(candidate.get());

    return new ResponseObject(200, "Follow success", converter.toDTO(candidate.get()));
  }

//  @Override
//  public ResponseObject applyJobPost(long candidateId, long jobPostId) {
//    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
//    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
//    if (candidate.isEmpty()) {
//      throw new NoSuchElementException("Candidate no value present");
//    }
//
//    if (jobPost.isEmpty()) {
//      throw new NoSuchElementException("Employer no value present");
//    }
//
//    candidate.get().get().add(jobPost.get());
//    candidateRepository.save(candidate.get());
//
//    return new ResponseObject(200, "Follow success", converter.toDTO(candidate.get()));
//  }
}
