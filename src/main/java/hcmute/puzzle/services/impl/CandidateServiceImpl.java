package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.PostCandidateRequest;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.CandidateMapper;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CandidateServiceImpl implements CandidateService {

  @Autowired
  CandidateRepository candidateRepository;

  @Autowired
  EmployerRepository employerRepository;

  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  CompanyRepository companyRepository;

  @Autowired
  CandidateMapper candidateMapper;

  @Override
  public CandidateDto save(CandidateDto candidateDto) {
    // casting provinceDTO to ProvinceEntity
    Candidate candidate = candidateMapper.candidateDtoToCandidate(candidateDto);

    // set id
    candidate.setId(0);

    if (candidate.getUser().getEmployer() != null) {
      throw new RuntimeException("This account for employer");
    }

    candidateRepository.save(candidate);

    Optional<User> userEntity = userRepository.findById(candidate.getId());
    if (userEntity.isEmpty()) {
      throw new CustomException("Account isn't exist");
    }
    Optional<Role> role = roleRepository.findById("candidate");
    if (role.isEmpty()) {
      throw new CustomException("role candidate isn't exist");
    }
    userEntity.get().getRoles().add(role.get());
    userRepository.save(userEntity.get());

    // return add Province success
    return candidateMapper.candidateToCandidateDto(candidate);
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
  public CandidateDto update(long candidateId, PostCandidateRequest postCandidateRequest) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(() -> new CustomException(
                                                     "Cannot find candidate with id = " + candidateId));
    if (!(postCandidateRequest.getEmailContact() != null && !postCandidateRequest.getEmailContact()
                                                                                 .isEmpty() && !postCandidateRequest.getEmailContact()
                                                                                                                    .isBlank())) {
      throw new CustomException("Email contact invalid");
    }
    //converter.toEntity(candidateDTO);
    // candidate.setId(candidate.getUserEntity().getId());
    candidateMapper.updateCandidateFromPostCandidateRequest(postCandidateRequest, candidate);
    candidateRepository.save(candidate);
    CandidateDto candidateDto = candidateMapper.candidateToCandidateDto(candidate);
    return candidateDto;

  }

  @Override
  public ResponseObject getOne(long id) {
    boolean exists = candidateRepository.existsById(id);

    if (exists) {
      Candidate candidate = candidateRepository.getReferenceById(id);
      CandidateDto candidateDTO = candidateMapper.candidateToCandidateDto(candidate);

      return new ResponseObject(200, "Info of candidate", candidateDTO);
    }

    throw new CustomException("Cannot find candidate with id = " + id);
  }

  @Override
  public ResponseObject followEmployer(long candidateId, long employerId) {
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);
    Optional<Employer> employer = employerRepository.findById(employerId);
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
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);
    Optional<Employer> employer = employerRepository.findById(employerId);
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
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);
    Optional<Company> company = companyRepository.findById(companyId);
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
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);
    Optional<Company> company = companyRepository.findById(companyId);
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
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);
    Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
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
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);
    Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
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
