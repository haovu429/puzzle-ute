package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.CompanyEntity;
import hcmute.puzzle.entities.EmployerEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.model.payload.request.company.CreateCompanyPayload;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.CompanyRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.CompanyService;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {
  @Autowired CompanyRepository companyRepository;

  @Autowired Converter converter;

  @Autowired FilesStorageService storageService;
  @Autowired CandidateRepository candidateRepository;

  @Override
  public ResponseObject save(CompanyDTO companyPayload, MultipartFile imageFile, EmployerEntity createEmployer) {

    CompanyEntity company = new CompanyEntity();
    company.setName(companyPayload.getName());
    company.setDescription(companyPayload.getDescription());
    company.setWebsite(companyPayload.getWebsite());
    company.setCreatedEmployer(createEmployer);
    company.setActive(companyPayload.isActive());
    company = companyRepository.save(company);

    if (imageFile != null || imageFile.getSize()>0) {
      String urlImage =
              storageService.updateAvatarReturnUrl(
                      company.getId(), imageFile, Constant.PREFIX_COMPANY_IMAGE_FILE_NAME);
      company.setImage(urlImage);
      company = companyRepository.save(company);
    }

    return new ResponseObject(
        200,
        "Sent request create info company to admin, please wait notify from admin",
        converter.toDTO(company));
  }

  @Override
  public ResponseObject update(CompanyDTO companyDTO) {

    boolean exists = companyRepository.existsById(companyDTO.getId());

    if (!exists) {
      throw new CustomException("Company isn't exists");
    }

    CompanyEntity companyEntity = converter.toEntity(companyDTO);
    companyEntity = companyRepository.save(companyEntity);
    return new ResponseObject(200, "Update successfully", converter.toDTO(companyEntity));
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
    Set<CompanyDTO> companyDTOS = new HashSet<>();
    companyRepository.findAll().stream()
        .forEach(
            company -> {
              companyDTOS.add(converter.toDTO(company));
            });

    return new ResponseObject(200, "Info company", companyDTOS);
  }

  @Override
  public ResponseObject getAllCompanyInActive() {
    Set<CompanyDTO> companyDTOS = new HashSet<>();
    companyRepository.findCompanyEntitiesByActiveFalse().stream()
        .forEach(
            company -> {
              companyDTOS.add(converter.toDTO(company));
            });

    return new ResponseObject(200, "Info company inactive", companyDTOS);
  }

  @Override
  public ResponseObject getOneById(long id) {
    Optional<CompanyEntity> company = companyRepository.findById(id);
    return new ResponseObject(200, "Info company", converter.toDTO(company.get()));
  }

  @Override
  public ResponseObject getCompanyFollowedByCandidateId(long candidateId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    Set<CompanyDTO> companyDTOS =
        candidate.get().getFollowingCompany().stream()
            .map(company -> converter.toDTO(company))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Company saved", companyDTOS);
  }

  public DataResponse getCreatedCompanyByEmployerId(long employerId) {
    List<CompanyDTO> companyDTOS =
        companyRepository.findByCreatedEmployer_Id(employerId).stream()
            .map(company -> converter.toDTO(company))
            .collect(Collectors.toList());
    return new DataResponse(companyDTOS);
  }
}
