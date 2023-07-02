package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.PostCandidateRequest;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.CandidateMapper;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User currentUser = customUserDetails.getUser();
    candidate.setUser(currentUser);
    // set id
    candidate.setId(0);

    if (candidate.getUser().getEmployer() != null) {
      throw new RuntimeException("This account for employer");
    }

    candidateRepository.save(candidate);

    User userEntity = userRepository.findById(candidate.getId())
                                    .orElseThrow(() -> new NotFoundDataException("Account isn't exist"));
    Role role = roleRepository.findById("candidate")
                              .orElseThrow(() -> new NotFoundDataException("role candidate isn't exist"));

    userEntity.getRoles().add(role);
    userRepository.save(userEntity);

    // return add Province success
    return candidateMapper.candidateToCandidateDto(candidate);
  }

  @Override
  public void delete(long id) {
    Candidate candidate = candidateRepository.findById(id)
                                             .orElseThrow(() -> new NotFoundDataException(
                                                     "Cannot find candidate with id =" + id));
    candidateRepository.delete(candidate);
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
  public CandidateDto getOne(long id) {
    Candidate candidate = candidateRepository.findById(id)
                                             .orElseThrow(() -> new NotFoundDataException(
                                                     "Cannot find candidate with id = " + id));

    CandidateDto candidateDTO = candidateMapper.candidateToCandidateDto(candidate);

    return candidateDTO;
  }

  @Override
  public void followEmployer(long candidateId, long employerId) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(
                                                     () -> new NotFoundDataException("Candidate no value present"));
    Employer employer = employerRepository.findById(employerId)
                                          .orElseThrow(() -> new NotFoundDataException("Employer no value present"));

    if (candidate.getFollowingEmployers().contains(employer)) {
      throw new CustomException("You already follow this employer");
    }

    candidate.getFollowingEmployers().add(employer);
    candidateRepository.save(candidate);

  }

  @Override
  public void cancelFollowedEmployer(long candidateId, long employerId) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(
                                                     () -> new NotFoundDataException("Candidate no value present"));
    Employer employer = employerRepository.findById(employerId)
                                          .orElseThrow(() -> new NotFoundDataException("Employer no value present"));

    if (!candidate.getFollowingEmployers().contains(employer)) {
      throw new CustomException("You haven't followed this employer yet");
    }

    candidate.getFollowingEmployers().remove(employer);
    candidateRepository.save(candidate);
  }

  @Override
  public void followCompany(long candidateId, long companyId) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(
                                                     (() -> new NotFoundDataException("Candidate no value present")));
    Company company = companyRepository.findById(companyId)
                                       .orElseThrow(() -> new NotFoundDataException("Company no value present"));


    if (candidate.getFollowingCompany().contains(company)) {
      throw new CustomException("You already follow this company");
    }

    candidate.getFollowingCompany().add(company);
    candidateRepository.save(candidate);
  }

  public void cancelFollowedCompany(long candidateId, long companyId) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(
                                                     (() -> new NotFoundDataException("Candidate no value present")));
    Company company = companyRepository.findById(companyId)
                                       .orElseThrow(() -> new NotFoundDataException("Company no value present"));


    if (!candidate.getFollowingCompany().contains(company)) {
      throw new CustomException("You haven't followed this company yet");
    }

    candidate.getFollowingCompany().remove(company);
    candidateRepository.save(candidate);
  }

  @Override
  public void saveJobPost(long candidateId, long jobPostId) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(
                                                     () -> new NotFoundDataException("Candidate no value present"));
    JobPost jobPost = jobPostRepository.findById(jobPostId)
                                       .orElseThrow(() -> new NotFoundDataException("JobPost no value present"));

    if (candidate.getSavedJobPost().contains(jobPost)) {
      throw new CustomException("You already save this Job Post");
    }

    candidate.getSavedJobPost().add(jobPost);
    candidateRepository.save(candidate);
  }

  @Override
  public void cancelSavedJobPost(long candidateId, long jobPostId) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(
                                                     () -> new NotFoundDataException("Candidate no value present"));
    JobPost jobPost = jobPostRepository.findById(jobPostId)
                                       .orElseThrow(() -> new NotFoundDataException("JobPost no value present"));
    if (!candidate.getSavedJobPost().contains(jobPost)) {
      throw new CustomException("You haven't saved this Job Post yet");
    }

    candidate.getSavedJobPost().remove(jobPost);
    candidateRepository.save(candidate);
  }
}
