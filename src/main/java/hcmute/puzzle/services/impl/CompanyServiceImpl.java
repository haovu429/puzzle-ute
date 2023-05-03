package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.FileStorageException;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.payload.request.company.CreateCompanyAdminDto;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.services.CompanyService;
import hcmute.puzzle.services.FilesStorageService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CompanyServiceImpl implements CompanyService {
  @Autowired CompanyRepository companyRepository;

  @Autowired Converter converter;

  @Autowired FilesStorageService storageService;
  @Autowired CandidateRepository candidateRepository;

  @Autowired FileRepository fileRepository;

  @Autowired UserRepository userRepository;

  @Autowired
  EmployerRepository employerRepository;

  @Override
  public DataResponse save(
          CompanyDto companyPayload, MultipartFile imageFile, EmployerEntity createEmployer)
      throws NotFoundException {

    CompanyEntity company = new CompanyEntity();
    company.setName(companyPayload.getName());
    company.setDescription(companyPayload.getDescription());
    company.setWebsite(companyPayload.getWebsite());
    company.setCreatedEmployer(createEmployer);
    company.setActive(companyPayload.isActive());
    company = companyRepository.save(company);

    if (imageFile != null && imageFile.getSize() > 0) {
      Optional<UserEntity> user = userRepository.findById(createEmployer.getId());

      if (user.isEmpty()) {
        throw new NotFoundException("Not found author");
      }

      // lay id sau khi luu vao db de dat ten cho anh
      FileEntity fileEntity =
          storageService
              .uploadFileWithFileTypeReturnUrl(
                  String.valueOf(company.getId()), imageFile, FileCategory.IMAGE_COMPANY)
              .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));

      company.setImage(fileEntity.getUrl());
      company = companyRepository.save(company);
    }

    return new DataResponse(converter.toDTO(company));
  }

  @Override
  public DataResponse createCompanyForAdmin(CreateCompanyAdminDto dto) {
    EmployerEntity createdEmployer = employerRepository.findById(dto.getCreatedEmployerId())
            .orElseThrow(() -> new NotFoundException("NOT_FOUND_CREATOR"));

    CompanyEntity newCompany = CompanyEntity.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .website(dto.getWebsite())
            .isActive(dto.isActive())
            .createdEmployer(createdEmployer)
            .build();
    newCompany = companyRepository.save(newCompany);

    if (dto.getImage() != null && dto.getImage().getSize() > 0) {
      UserEntity user = userRepository.findById(dto.getCreatedEmployerId())
              .orElseThrow(() -> new NotFoundException("NOT_FOUND_USER"));

      // lay id sau khi luu vao db de dat ten cho anh
      FileEntity fileEntity =
              storageService
                      .uploadFileWithFileTypeReturnUrl(
                              String.valueOf(newCompany.getId()), dto.getImage(), FileCategory.IMAGE_COMPANY)
                      .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));

      newCompany.setImage(fileEntity.getUrl());
      newCompany = companyRepository.save(newCompany);
    }
    return new DataResponse(converter.toDTO(newCompany));
  }

  @Override
  public DataResponse update(
      long companyId,
      CompanyDto companyPayload,
      MultipartFile imageFile,
      EmployerEntity createEmployer)
      throws NotFoundException {
    Optional<CompanyEntity> companyEntityOptional = companyRepository.findById(companyId);

    if (companyEntityOptional.isEmpty()) {
      throw new CustomException("Company not found");
    }

    CompanyEntity company = companyEntityOptional.get();
    if (companyPayload.getName() != null) {
      company.setName(companyPayload.getName());
    }

    if (companyPayload.getDescription() != null) {
      company.setDescription(companyPayload.getDescription());
    }

    if (companyPayload.getWebsite() != null) {
      company.setWebsite(companyPayload.getWebsite());
    }

    if (createEmployer != null) {
      company.setCreatedEmployer(createEmployer);
    }

    if (companyPayload.isActive() != company.isActive()) {
      company.setActive(companyPayload.isActive());
    }

    company = companyRepository.save(company);

    if (imageFile != null && imageFile.getSize() > 0) {

      // lay id sau khi luu vao db de dat ten cho anh
      FileEntity savedFile =
          storageService
              .uploadFileWithFileTypeReturnUrl(
                  String.valueOf(company.getId()), imageFile, FileCategory.IMAGE_COMPANY)
              .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));
      company.setImage(savedFile.getUrl());
      company = companyRepository.save(company);
      FileEntity fileEntity =
          fileRepository.findAllByUrlAndDeletedIs(savedFile.getUrl(), false).orElse(null);
      // Update object id for image
      if (fileEntity != null) {
        fileEntity.setObjectId(company.getId());
        fileRepository.save(fileEntity);
      }
    }

    return new DataResponse(converter.toDTO(company));
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
    Set<CompanyDto> companyDtos = new HashSet<>();
    companyRepository.findAll().stream()
        .forEach(
            company -> {
              companyDtos.add(converter.toDTO(company));
            });

    return new ResponseObject(200, "Info company", companyDtos);
  }

  @Override
  public ResponseObject getAllCompanyInActive() {
    Set<CompanyDto> companyDtos = new HashSet<>();
    companyRepository.findCompanyEntitiesByActiveFalse().stream()
        .forEach(
            company -> {
              companyDtos.add(converter.toDTO(company));
            });

    return new ResponseObject(200, "Info company inactive", companyDtos);
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

    Set<CompanyDto> companyDtos =
        candidate.get().getFollowingCompany().stream()
            .map(company -> converter.toDTO(company))
            .collect(Collectors.toSet());

    return new ResponseObject(200, "Company saved", companyDtos);
  }

  public DataResponse getCreatedCompanyByEmployerId(long employerId) {
    List<CompanyDto> companyDtos =
        companyRepository.findByCreatedEmployer_Id(employerId).stream()
            .map(company -> converter.toDTO(company))
            .collect(Collectors.toList());
    return new DataResponse(companyDtos);
  }
}
