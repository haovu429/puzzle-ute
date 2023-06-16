package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.EmployerMapper;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.EmployerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployerServiceImpl implements EmployerService {

  @Autowired
  CandidateRepository candidateRepository;

  @Autowired
  EmployerRepository employerRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  ApplicationRepository applicationRepository;

  @Autowired
  RoleRepository roleRepository;

  //  @Autowired
  //  Converter converter;

  @Autowired
  EmployerMapper employerMapper;

  @PersistenceContext
  public EntityManager em;

  @Override
  public EmployerDto save(EmployerDto employerDTO) {
    // casting provinceDTO to ProvinceEntity
    Employer employer = employerMapper.employerDtoToEmployer(employerDTO);

    // save province
    //    employer.setId(0);
    if (employer.getUser().getCandidate() != null) {
      throw new RuntimeException("This account for candidate");
    }

    employerRepository.save(employer);

    User user = userRepository.findById(employer.getId())
                              .orElseThrow(() -> new NotFoundDataException("Account isn't exist"));

    Role role = roleRepository.findById("employer")
                              .orElseThrow(() -> new NotFoundDataException("role candidate isn't exist"));

    user.getRoles().add(role);
    userRepository.save(user);

    EmployerDto employerDto = employerMapper.employerToEmployerDto(employer);

    return employerDto;
  }

  @Override
  public void delete(long id) {
    Employer employer = employerRepository.findById(id)
                                          .orElseThrow(() -> new NotFoundDataException("Not found employer"));
    employerRepository.delete(employer);
  }

  @Override
  public EmployerDto update(EmployerDto employerDTO) {
    Employer employer = employerRepository.findById(employerDTO.getId())
                                          .orElseThrow(() -> new NotFoundDataException("Not found employer"));
    employerMapper.updateEmployerFromEmployerDto(employerDTO, employer);
    return employerMapper.employerToEmployerDto(employer);
  }

  @Override
  public EmployerDto getOne(long id) {
    Employer employer = employerRepository.findById(id)
                                          .orElseThrow(() -> new NotFoundDataException(
                                                  "Cannot find employer with id = " + id));

    return employerMapper.employerToEmployerDto(employer);
  }

  @Override
  public List<EmployerDto> getEmployerFollowedByCandidateId(long candidateId) {
    Candidate candidate = candidateRepository.findById(candidateId)
                                             .orElseThrow(() -> new NotFoundDataException("Candidate isn't exist"));


    List<EmployerDto> employerDtos = candidate.getFollowingEmployers()
                                              .stream()
                                              .map(employerMapper::employerToEmployerDto)
                                              .toList();

    return employerDtos;
  }

  //markingJobpostWasDeleted

  @Override
  public double getApplicationRateEmployerId(long employerId) {
    Optional<Employer> employer = employerRepository.findById(employerId);
    if (employer.isEmpty()) {
      throw new CustomException("Cannot find employer with id = " + employerId);
    }

    double rate = 0; // mac dinh, hoi sai neu view = 0 và application > 0
    long viewOfCandidateAmount = getViewedCandidateAmountToJobPostCreatedByEmployer(employerId);
    long applicationOfCandidateAmount = applicationRepository.getAmountApplicationToEmployer(employerId);

    if (viewOfCandidateAmount != 0 && viewOfCandidateAmount >= applicationOfCandidateAmount) {
      rate = Double.valueOf(applicationOfCandidateAmount)/viewOfCandidateAmount;
      rate = rate * 100; // doi ti le ra phan tram
      // lam tron 2 chu so thap phan
      rate = Math.round(rate * 100.0) / 100.0;
    }

    return rate;
  }

  public long getViewedCandidateAmountToJobPostCreatedByEmployer(long employerId) {
    long amount = 0;
    // check subscribes of employer
    String sql = "SELECT COUNT(u.id) FROM JobPost jp INNER JOIN jp.viewedUsers u WHERE jp.createdEmployer.id =:employerId AND u.candidate.id IS NOT NULL AND jp.isDeleted=FALSE";
    try {
      amount =
              (long) em.createQuery(sql).setParameter("employerId", employerId).getSingleResult();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return amount;
  }
}
