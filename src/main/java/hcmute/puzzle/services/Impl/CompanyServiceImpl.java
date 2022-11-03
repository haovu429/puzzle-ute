package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CompanyEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.CompanyRepository;
import hcmute.puzzle.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CompanyServiceImpl implements CompanyService {
  @Autowired CompanyRepository companyRepository;

  @Autowired Converter converter;

  @Override
  public ResponseObject save(CompanyDTO companyDTO) {

    companyDTO.setId(0);

    CompanyEntity companyEntity = converter.toEntity(companyDTO);
    companyEntity = companyRepository.save(companyEntity);

    return new ResponseObject(
        200,
        "Sent request create info company to admin, please wait notify from admin",
        converter.toDTO(companyEntity));
  }

  @Override
  public ResponseObject update(CompanyDTO companyDTO) {

      boolean exists = companyRepository.existsById(companyDTO.getId());

      if (!exists) {
          throw new CustomException("Company isn't exists");
      }

      CompanyEntity companyEntity = converter.toEntity(companyDTO);
      companyEntity = companyRepository.save(companyEntity);
    return new ResponseObject(200, "Update successfully", companyEntity);
  }

  @Override
  public ResponseObject delete(long id) {
      boolean exists = companyRepository.existsById(id);

      if (!exists) {
          throw new CustomException("Company isn't exists");
      }

      companyRepository.deleteById(id);

      return new ResponseObject(200, "Delete successfully", null);
  }

  @Override
  public ResponseObject getAll() {
      Set<CompanyEntity> companyEntities = new HashSet<>();
      companyRepository.findAll().stream().forEach(company -> {
          companyEntities.add(company);
      });

      return new ResponseObject(200, "Info company", companyEntities);
  }

    @Override
    public ResponseObject getOneById(long id) {
        Optional<CompanyEntity> company = companyRepository.findById(id);
        return new ResponseObject(200, "Info company", company.get());
    }
}
