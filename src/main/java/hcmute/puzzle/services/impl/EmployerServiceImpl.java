package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.repository.*;

import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.services.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

  @Autowired Converter converter;

  @PersistenceContext
  public EntityManager em;

  @Override
  public Optional<EmployerDto> save(EmployerDto employerDTO) {
    // casting provinceDTO to ProvinceEntity
    Employer employer = converter.toEntity(employerDTO);

    // save province
    employer.setId(0);

    if (employer.getUser().getCandidate() != null) {
      throw new RuntimeException("This account for candidate");
    }

    employerRepository.save(employer);

    Optional<User> userEntity = userRepository.findById(employer.getId());
    if (userEntity.isEmpty()) {
      throw new CustomException("Account isn't exist");
    }
    Optional<Role> role = roleRepository.findById("employer");
    if (role.isEmpty()) {
      throw new CustomException("role candidate isn't exist");
    }
    userEntity.get().getRoles().add(role.get());
    userRepository.save(userEntity.get());

    Optional<EmployerDto> result = Optional.of(converter.toDTO(employer));

    return result;
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = employerRepository.existsById(id);
    if (exists) {
      employerRepository.deleteById(id);
      return new ResponseObject(200, "Delete employer Successfully", null);
    }
    throw new CustomException("Cannot find employer with id =" + id);
  }

  @Override
  public ResponseObject update(EmployerDto employerDTO) {

    boolean exists = employerRepository.existsById(employerDTO.getId());

    if (exists) {
      Employer employer = converter.toEntity(employerDTO);
      // employer.setId(employer.getUserEntity().getId());

      employerRepository.save(employer);
      return new ResponseObject(converter.toDTO(employer));
    }

    throw new CustomException("Cannot find employer with id = " + employerDTO.getId());
  }

  @Override
  public ResponseObject getOne(long id) {
    boolean exists = employerRepository.existsById(id);

    if (exists) {
      Employer employer = employerRepository.getReferenceById(id);

      return new ResponseObject(200, "Info of employer", converter.toDTO(employer));
    }

    throw new CustomException("Cannot find employer with id = " + id);
  }

  @Override
  public ResponseObject getEmployerFollowedByCandidateId(long candidateId) {
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    Set<EmployerDto> employerDtos =
            candidate.get().getFollowingEmployers().stream()
                    .map(employer -> converter.toDTO(employer))
                    .collect(Collectors.toSet());

    return new ResponseObject(200, "Employer followed", employerDtos);
  }

  //markingJobpostWasDeleted

  @Override
  public DataResponse getApplicationRateEmployerId(long employerId) {
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

    return new DataResponse(rate);
  }

  public long getViewedCandidateAmountToJobPostCreatedByEmployer(long employerId) {
    long amount = 0;
    // check subscribes of employer
    String sql =
            "SELECT COUNT(u.id) FROM JobPost jp INNER JOIN jp.viewedUsers u WHERE jp.createdEmployer.id =:employerId AND u.candidateEntity.id IS NOT NULL AND jp.isDeleted=FALSE";
    try {
      amount =
              (long) em.createQuery(sql).setParameter("employerId", employerId).getSingleResult();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return amount;
  }
}
