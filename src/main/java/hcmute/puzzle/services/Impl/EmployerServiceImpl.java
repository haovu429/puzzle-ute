package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.EmployerEntity;
import hcmute.puzzle.entities.RoleEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.EmployerRepository;
import hcmute.puzzle.repository.RoleRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployerServiceImpl implements EmployerService {

  @Autowired CandidateRepository candidateRepository;

  @Autowired EmployerRepository employerRepository;

  @Autowired UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired Converter converter;

  @Override
  public Optional<EmployerDTO> save(EmployerDTO employerDTO) {
    // casting provinceDTO to ProvinceEntity
    EmployerEntity employerEntity = converter.toEntity(employerDTO);

    // save province
    employerEntity.setId(0);

    if (employerEntity.getUserEntity().getCandidateEntity() != null) {
      throw new RuntimeException("This account for candidate");
    }

    employerRepository.save(employerEntity);

    Optional<UserEntity> userEntity = userRepository.findById(employerEntity.getId());
    if (userEntity.isEmpty()) {
      throw new CustomException("Account isn't exist");
    }
    Optional<RoleEntity> role = roleRepository.findById("candidate");
    if (role.isEmpty()) {
      throw new CustomException("role candidate isn't exist");
    }
    userEntity.get().getRoles().add(role.get());
    userRepository.save(userEntity.get());

    Optional<EmployerDTO> result = Optional.of(converter.toDTO(employerEntity));

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
  public ResponseObject update(EmployerDTO employerDTO) {

    boolean exists = employerRepository.existsById(employerDTO.getId());

    if (exists) {
      EmployerEntity employer = converter.toEntity(employerDTO);
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
      EmployerEntity employer = employerRepository.getReferenceById(id);

      return new ResponseObject(200, "Info of employer", converter.toDTO(employer));
    }

    throw new CustomException("Cannot find employer with id = " + id);
  }

  @Override
  public ResponseObject getEmployerFollowedByCandidateId(long candidateId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    Set<EmployerDTO> employerDTOS =
            candidate.get().getFollowingEmployers().stream()
                    .map(employer -> converter.toDTO(employer))
                    .collect(Collectors.toSet());

    return new ResponseObject(200, "Employer followed", employerDTOS);
  }
}
