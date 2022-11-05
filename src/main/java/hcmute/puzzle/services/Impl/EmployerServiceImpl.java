package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.EmployerEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.EmployerRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployerServiceImpl implements EmployerService {

  @Autowired CandidateRepository candidateRepository;

  @Autowired EmployerRepository employerRepository;

  @Autowired UserRepository userRepository;

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

    // Optional<UserEntity> user = userRepository.findById(em)
    employerRepository.save(employerEntity);

    Optional<EmployerDTO> result = Optional.of(converter.toDTO(employerEntity));

    // return add Province success
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

    throw new CustomException("Cannot find candidate with id = " + id);
  }
}
