package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.CompanyMapper;
import hcmute.puzzle.infrastructure.models.CompanyFilter;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.CompanyService;
import hcmute.puzzle.services.FilesStorageService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {
  @Autowired
  CompanyRepository companyRepository;

  //  @Autowired
  //  Converter converter;

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
  public CompanyResponse save(CompanyDto companyDto) throws NotFoundException {
    CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                                   .getAuthentication()
                                                                                   .getPrincipal();
    validateCompanyInfo(companyDto);
    User currentUser = customUserDetails.getUser();
    Employer currentEmployer = currentUser.getEmployer();
    MultipartFile imageFile = companyDto.getImageFile();
    Company company = Company.builder().name(companyDto.getName()).description(companyDto.getDescription())
                             .website(companyDto.getWebsite())
                             .createdEmployer(currentEmployer)
                             .isPublic(companyDto.getIsPublic())
                             .build();
    if (currentUser.getRoles().stream().anyMatch(role -> role.getCode().equalsIgnoreCase(Roles.ADMIN.getValue()))) {
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
    CompanyResponse companyResponse = companyMapper.companyToCompanyResponse(company);
    return companyResponse;
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
  public CompanyResponse update(long companyId, CompanyDto companyPayload, MultipartFile imageFile,
          Employer createEmployer) throws NotFoundException {
    Company companyEntityOptional = companyRepository.findById(companyId)
                                                     .orElseThrow(() -> new NotFoundDataException("Company not found"));

    Company company = companyEntityOptional;
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

    return companyMapper.companyToCompanyResponse(company);
  }

  @Override
  public void delete(long id) {
    Company company = companyRepository.findById(id)
                                       .orElseThrow(() -> new NotFoundDataException("Company isn't exists"));
    companyRepository.delete(company);
  }

  @Override
  public Page<CompanyResponse> getAll(Pageable pageable) {
    Page<CompanyResponse> companyResponses = companyRepository.findAll(pageable)
                                                              .map(companyMapper::companyToCompanyResponse);
    return companyResponses;
  }

  @Override
  public Page<CompanyResponse> filterCompany(CompanyFilter companyFilter, Pageable pageable) {
    Specification<Company> companySpecification = doPredicate(companyFilter);
    Page<CompanyResponse> companyResponses = companyRepository.findAll(companySpecification, pageable)
                                                              .map(companyMapper::companyToCompanyResponse);
    return companyResponses;
  }


  @Override
  public CompanyResponse getOneById(long id) {
    Company company = companyRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found company"));
    return companyMapper.companyToCompanyResponse(company);
  }

  @Override
  public List<CompanyResponse> getCompanyFollowedByCandidateId(long candidateId) {
    Optional<Candidate> candidate = candidateRepository.findById(candidateId);

    if (candidate.isEmpty()) {
      throw new CustomException("Candidate isn't exist");
    }

    List<CompanyResponse> companyDtos = candidate.get()
                                                 .getFollowingCompany()
                                                 .stream()
                                                 .map(companyMapper::companyToCompanyResponse)
                                                 .toList();

    return companyDtos;
  }

  public List<CompanyResponse> getCreatedCompanyByEmployerId(long employerId) {
    List<CompanyResponse> companyResponses = companyRepository.findByCreatedEmployer_Id(employerId)
                                                              .stream()
                                                              .map(companyMapper::companyToCompanyResponse)
                                                              .collect(Collectors.toList());
    return companyResponses;
  }

  public Specification<Company> doPredicate(CompanyFilter companyFilter) {

    return ((root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new LinkedList<>();

      // Is Active
      if (companyFilter.getIsActive() != null) {
        Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isActive"), companyFilter.getIsActive());
        predicates.add(withCheckActiveFromSystem);
      }

      // Is Public
      if (companyFilter.getIsActive() != null) {
        Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isPublic"), companyFilter.getIsPublic());
        predicates.add(withCheckActiveFromSystem);
      }

      // Search Key
      if (companyFilter.getSearchKey() != null) {
        String keyword = companyFilter.getSearchKey();
        List<Predicate> or = new ArrayList<>();
        Predicate orInTitle = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword + "%");
        Predicate orInDescription = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                                                         "%" + keyword + "%");
        Predicate orInWebsite = criteriaBuilder.like(criteriaBuilder.lower(root.get("website")), "%" + keyword + "%");
        or.add(orInTitle);
        or.add(orInDescription);
        or.add(orInWebsite);
        predicates.add(criteriaBuilder.or(or.toArray(new Predicate[0])));
      }

      //Create Time
      if (companyFilter.getCreatedAtFrom() != null) {
        Timestamp tsCreatedAtFrom = new Timestamp(companyFilter.getCreatedAtFrom().getTime());
        Predicate withCreatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), tsCreatedAtFrom);
        predicates.add(criteriaBuilder.and(withCreatedAtFrom));
      }

      if (companyFilter.getCreatedAtTo() != null) {
        Timestamp tsCreatedAtTo = new Timestamp(companyFilter.getCreatedAtTo().getTime());
        Predicate withCreatedAtTo = criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), tsCreatedAtTo);
        predicates.add(criteriaBuilder.and(withCreatedAtTo));
      }

      // Update time
      if (companyFilter.getUpdatedAtFrom() != null) {
        Timestamp tsUpdatedAtFrom = new Timestamp(companyFilter.getUpdatedAtFrom().getTime());
        Predicate withUpdatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), tsUpdatedAtFrom);
        predicates.add(criteriaBuilder.and(withUpdatedAtFrom));
      }

      if (companyFilter.getUpdatedAtTo() != null) {
        Timestamp tsUpdatedAtTo = new Timestamp(companyFilter.getUpdatedAtTo().getTime());
        Predicate withUpdatedAtTo = criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), tsUpdatedAtTo);
        predicates.add(criteriaBuilder.and(withUpdatedAtTo));
      }

      // Sort
      if (Objects.nonNull(companyFilter.getIsAscSort()) && companyFilter.getIsAscSort()
                                                                        .equals(true) && companyFilter.getSortColumn() != null && !companyFilter.getSortColumn()
                                                                                                                                                .isBlank()) {
        switch (companyFilter.getSortColumn()) {
          case "createdAt":
            companyFilter.setSortColumn("createdAt");
            query.orderBy(criteriaBuilder.asc(root.get(companyFilter.getSortColumn())));
            break;
          case "updatedAt":
            companyFilter.setSortColumn("updatedAt");
            query.orderBy(criteriaBuilder.asc(root.get(companyFilter.getSortColumn())));
            break;
          default:
            query.orderBy(criteriaBuilder.asc(root.get(companyFilter.getSortColumn())));
        }
      } else if (Objects.nonNull(companyFilter.getIsAscSort()) && companyFilter.getIsAscSort()
                                                                               .equals(true) && companyFilter.getSortColumn() != null && !companyFilter.getSortColumn()
                                                                                                                                                       .isBlank()) {
        switch (companyFilter.getSortColumn()) {
          case "createdAt":
            companyFilter.setSortColumn("createdAt");
            query.orderBy(criteriaBuilder.desc(root.get(companyFilter.getSortColumn())));
            break;
          case "updatedAt":
            companyFilter.setSortColumn("updatedAt");
            query.orderBy(criteriaBuilder.desc(root.get(companyFilter.getSortColumn())));
            break;
          default:
            query.orderBy(criteriaBuilder.desc(root.get(companyFilter.getSortColumn())));
        }
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
    });
  }
}
