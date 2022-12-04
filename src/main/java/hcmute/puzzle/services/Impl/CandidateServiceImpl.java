package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.*;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.*;
import hcmute.puzzle.services.CandidateService;
import hcmute.puzzle.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CandidateServiceImpl implements CandidateService {

  @Autowired CandidateRepository candidateRepository;

  @Autowired EmployerRepository employerRepository;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired UserRepository userRepository;

  @Autowired RoleRepository roleRepository;

  @Autowired Converter converter;

  @Autowired CompanyRepository companyRepository;

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

    Optional<UserEntity> userEntity = userRepository.findById(candidateEntity.getId());
    if (userEntity.isEmpty()) {
      throw new CustomException("Account isn't exist");
    }
    Optional<RoleEntity> role = roleRepository.findById("candidate");
    if (role.isEmpty()) {
      throw new CustomException("role candidate isn't exist");
    }
    userEntity.get().getRoles().add(role.get());
    userRepository.save(userEntity.get());

    Optional<CandidateDTO> result = Optional.of(converter.toDTO(candidateEntity));

    // return add Province success
    return result;
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
      CandidateDTO candidateDTO = converter.toDTO(candidate);

      return new ResponseObject(200, "Info of candidate", candidateDTO);
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

    if (candidate.get().getFollowingEmployers().contains(employer.get())) {
      throw new CustomException("You already follow this employer");
    }

    candidate.get().getFollowingEmployers().add(employer.get());
    candidateRepository.save(candidate.get());

    return new ResponseObject(200, "Follow success", null);
  }

  @Override
  public ResponseObject cancelFollowedEmployer(long candidateId, long employerId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<EmployerEntity> employer = employerRepository.findById(employerId);
    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (employer.isEmpty()) {
      throw new NoSuchElementException("Employer no value present");
    }

    if (!candidate.get().getFollowingEmployers().contains(employer.get())) {
      throw new CustomException("You haven't followed this employer yet");
    }

    candidate.get().getFollowingEmployers().remove(employer.get());
    candidateRepository.save(candidate.get());

    return new ResponseObject(200, "Cancel follow success", null);
  }

  @Override
  public ResponseObject followCompany(long candidateId, long companyId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<CompanyEntity> company = companyRepository.findById(companyId);
    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (company.isEmpty()) {
      throw new NoSuchElementException("Company no value present");
    }

    if (candidate.get().getFollowingCompany().contains(company.get())) {
      throw new CustomException("You already follow this company");
    }

    candidate.get().getFollowingCompany().add(company.get());
    candidateRepository.save(candidate.get());

    return new ResponseObject(200, "Follow success", null);
  }

  public ResponseObject cancelFollowedCompany(long candidateId, long companyId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<CompanyEntity> company = companyRepository.findById(companyId);
    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (company.isEmpty()) {
      throw new NoSuchElementException("Company no value present");
    }

    if (!candidate.get().getFollowingCompany().contains(company.get())) {
      throw new CustomException("You haven't followed this company yet");
    }

    candidate.get().getFollowingCompany().remove(company.get());
    candidateRepository.save(candidate.get());

    return new ResponseObject(200, "Cancel follow success", null);
  }

  @Override
  public ResponseObject saveJobPost(long candidateId, long jobPostId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (jobPost.isEmpty()) {
      throw new NoSuchElementException("JobPost no value present");
    }

    if (candidate.get().getSavedJobPost().contains(jobPost.get())) {
      throw new CustomException("You already save this Job Post");
    }

    candidate.get().getSavedJobPost().add(jobPost.get());
    candidateRepository.save(candidate.get());

    return new ResponseObject(200, "Save success", null);
  }

  @Override
  public ResponseObject cancelSavedJobPost(long candidateId, long jobPostId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (jobPost.isEmpty()) {
      throw new NoSuchElementException("JobPost no value present");
    }

    if (!candidate.get().getSavedJobPost().contains(jobPost.get())) {
      throw new CustomException("You haven't saved this Job Post yet");
    }

    candidate.get().getSavedJobPost().remove(jobPost.get());
    candidateRepository.save(candidate.get());

    return new ResponseObject(200, "Cancel saved Job Post success", null);
  }
}
