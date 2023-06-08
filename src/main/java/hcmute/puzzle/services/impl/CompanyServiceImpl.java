package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.CompanyMapper;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.CompanyService;
import hcmute.puzzle.services.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {
  @Autowired
  CompanyRepository companyRepository;

  @Autowired
  Converter converter;

  @Autowired
  FilesStorageService storageService;
  @Autowired
  CandidateRepository candidateRepository;

  @Autowired
  FileRepository fileRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  EmployerRepository employerRepository;

  @Autowired
  CompanyMapper companyMapper;

  private void validateCompanyInfo(CompanyDto companyDto) {
    List<Company> companies = companyRepository.findCompaniesByName(companyDto.getName());
    if (companies != null) {
      if (!companies.isEmpty()) {
        throw new AlreadyExistsException("Company name is already exists");
      }
    }
  }

  @Transactional
  @Override
  public DataResponse save(CompanyDto companyDto) throws NotFoundException {
    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                                   .getAuthentication()
                                                                                   .getPrincipal();
    validateCompanyInfo(companyDto);
    User currentUser = customUserDetails.getUser();
    Employer currentEmployer = currentUser.getEmployer();
    MultipartFile imageFile = companyDto.getImageFile();
    Company company = Company.builder()
                             .name(companyDto.getName())
                             .description(companyDto.getDescription())
                             .website(companyDto.getWebsite())
                             .createdEmployer(currentEmployer)
                             .isPublic(companyDto.getIsPublic())
                             .build();
    if (currentUser.getRoles().stream().anyMatch(role -> role.getCode().equalsIgnoreCase(Roles.ADMIN.value))) {
      if (companyDto.getIsActive() != null) {
        company.setIsActive(companyDto.getIsActive());
      }

      // Specific employer own
      if (companyDto.getCreatedEmployerId() != null) {
        Employer employer = employerRepository.findById(companyDto.getCreatedEmployerId())
                                              .orElseThrow(() -> new NotFoundDataException("Not found employer"));
        company.setCreatedEmployer(employer);
      }
    }
    //    company.setName(companyPayload.getName());
    //    company.setDescription(companyPayload.getDescription());
    //    company.setWebsite(companyPayload.getWebsite());
    //    company.setCreatedEmployer(createEmployer);
    //    company.setActive(companyPayload.isActive());
    company = companyRepository.save(company);

    if (imageFile != null && imageFile.getSize() > 0) {
      // lay id sau khi luu vao db de dat ten cho anh
      String imageUrl = storageService.uploadFileWithFileTypeReturnUrl(String.valueOf(company.getId()), imageFile,
                                                                       FileCategory.IMAGE_COMPANY, true)
                                      .orElseThrow(() -> new FileStorageException("upload image fail"));
      company.setImage(imageUrl);
      company = companyRepository.save(company);
    }
    CompanyResponse companyResponse = companyMapper.companyToUserCompanyResponse(company);
    return new DataResponse<>(companyResponse);
  }

  //  @Override
  //  public DataResponse createCompanyForAdmin(CreateCompanyAdminRequest dto) {
  //    Employer createdEmployer = employerRepository.findById(dto.getCreatedEmployerId())
  //                                                 .orElseThrow(() -> new NotFoundException("NOT_FOUND_CREATOR"));
  //
  //    Company newCompany = Company.builder()
  //                                .name(dto.getName())
  //                                .description(dto.getDescription())
  //                                .website(dto.getWebsite())
  //                                .isActive(dto.isActive())
  //                                .createdEmployer(createdEmployer)
  //                                .build();
  //    newCompany = companyRepository.save(newCompany);
  //
  //    if (dto.getImage() != null && dto.getImage().getSize() > 0) {
  //      // lay id sau khi luu vao db de dat ten cho anh
  //      String imageUrl = storageService.uploadFileWithFileTypeReturnUrl(String.valueOf(newCompany.getId()),
  //                                                                       dto.getImage(), FileCategory.IMAGE_COMPANY, true)
  //                                      .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));
  //
  //      newCompany.setImage(imageUrl);
  //      newCompany = companyRepository.save(newCompany);
  //    }
  //    return new DataResponse<>(converter.toDTO(newCompany));
  //  }

  @Override
  public DataResponse update(long companyId, CompanyDto companyPayload, MultipartFile imageFile,
          Employer createEmployer) throws NotFoundException {
    Optional<Company> companyEntityOptional = companyRepository.findById(companyId);

    if (companyEntityOptional.isEmpty()) {
      throw new CustomException("Company not found");
    }

    Company company = companyEntityOptional.get();
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

    //    if (companyPayload.isActive() != company.isActive()) {
    //      company.setActive(companyPayload.isActive());
    //    }

    company = companyRepository.save(company);

    if (imageFile != null && imageFile.getSize() > 0) {

      // lay id sau khi luu vao db de dat ten cho anh
      String imageUrl = storageService.uploadFileWithFileTypeReturnUrl(String.valueOf(company.getId()), imageFile,
                                                                       FileCategory.IMAGE_COMPANY, true)
                                      .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));
      company.setImage(imageUrl);
      company = companyRepository.save(company);
      File file = fileRepository.findAllByUrlAndDeletedIs(imageUrl, false).orElse(null);
      // Update object id for image
      if (file != null) {
        file.setObjectId(company.getId());
        fileRepository.save(file);
      }
    }

    return new DataResponse<>(converter.toDTO(company));
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

    return new ResponseObject<>(200, "Info company", companyDtos);
  }

  @Override
  public ResponseObject getAllCompanyInActive() {
    Set<CompanyDto> companyDtos = new HashSet<>();
    companyRepository.findCompanyEntitiesByActiveFalse().stream()
        .forEach(
            company -> {
              companyDtos.add(converter.toDTO(company));
            });

    return new ResponseObject<>(200, "Info company inactive", companyDtos);
  }

  @Override
  public ResponseObject getOneById(long id) {
    Optional<Company> company = companyRepository.findById(id);
    return new ResponseObject<>(200, "Info company", converter.toDTO(company.get()));
  }

  @Override
  public ResponseObject getCompanyFollowedByCandidateId(long candidateId) {
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    Set<CompanyDto> companyDtos =
        candidate.get().getFollowingCompany().stream()
            .map(company -> converter.toDTO(company))
            .collect(Collectors.toSet());

    return new ResponseObject<>(200, "Company saved", companyDtos);
  }

  public DataResponse getCreatedCompanyByEmployerId(long employerId) {
    List<CompanyDto> companyDtos =
        companyRepository.findByCreatedEmployer_Id(employerId).stream()
            .map(company -> converter.toDTO(company))
            .collect(Collectors.toList());
    return new DataResponse<>(companyDtos);
  }
}
