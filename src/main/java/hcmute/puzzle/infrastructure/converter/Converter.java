package hcmute.puzzle.infrastructure.converter;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.*;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.utils.Provider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Converter {

  @Autowired private ApplicationRepository applicationRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired private CandidateRepository candidateRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private DocumentRepository documentRepository;
  @Autowired private EmployerRepository employerRepository;
  @Autowired private EvaluateRepository evaluateRepository;
  @Autowired private ExperienceRepository experienceRepository;
  @Autowired private FileRepository fileRepository;
  @Autowired private JobAlertRepository jobAlertRepository;
  @Autowired private JobPostRepository jobPostRepository;
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private ModelMapper modelMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private BlogPostRepository blogPostRepository;
  @Autowired private CommentRepository commentRepository;

  public Converter() {
  }

  // User
  public UserPostDto toDTO(UserEntity entity) {
    UserPostDto userPostDTO = new UserPostDto();
    userPostDTO.setId(entity.getId());
    userPostDTO.setUsername(entity.getUsername());
    userPostDTO.setEmail(entity.getEmail());
    userPostDTO.setPhone(entity.getPhone());
    userPostDTO.setAvatar(entity.getAvatar());
   // userPostDTO.setJoinDate(entity.getCreatedAt());
    userPostDTO.setLastLoginAt(entity.getLastLoginAt());
    userPostDTO.setActive(entity.isActive());
    userPostDTO.setProvider(entity.getProvider().toString());
    userPostDTO.setFullName(entity.getFullName());
    userPostDTO.setEmailVerified(entity.isEmailVerified());
    userPostDTO.setLocale(entity.getLocale());

    if (!entity.getRoles().isEmpty()) {
      userPostDTO.setRoleCodes(
          entity.getRoles().stream().map(role -> role.getCode()).collect(Collectors.toSet()).stream().collect(Collectors.toList()));
    }

    return userPostDTO;
  }

  public UserEntity toEntity(UserPostDto dto) {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(dto.getId());
    userEntity.setUsername(dto.getUsername());
    userEntity.setEmail(dto.getEmail());
    userEntity.setPhone(dto.getPhone());
    userEntity.setAvatar(dto.getAvatar());
    userEntity.setLastLoginAt(dto.getLastLoginAt());
    userEntity.setActive(dto.isActive());
    userEntity.setProvider(Provider.asProvider(dto.getProvider()));
    userEntity.setFullName(dto.getFullName());
    userEntity.setEmailVerified(dto.isEmailVerified());
    userEntity.setLocale(dto.getLocale());

    Set<RoleEntity> roleEntities = new HashSet<>();
    for (String code : dto.getRoleCodes()) {
      if (roleRepository.existsById(code)) {
        roleEntities.add(roleRepository.findOneByCode(code));
      } else {
        throw new CustomException("Can't convert! Not found role has role code = " + code);
      }
    }
    userEntity.setRoles(roleEntities);

    return userEntity;
  }

  //  Role
  public RoleDto toDTO(RoleEntity entity) {
    RoleDto roleDTO = RoleDto.builder().code(entity.getCode()).name(entity.getName()).build();
    return roleDTO;
  }

  public RoleEntity toEntity(RoleDto dto) {
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setCode(dto.getCode());
    roleEntity.setName(dto.getName());
    return roleEntity;
  }

  // ApplicationEntity
  public ApplicationDto toDTO(ApplicationEntity entity) {
    ApplicationDto applicationDTO = new ApplicationDto();
    applicationDTO.setId(entity.getId());
    applicationDTO.setResult(entity.getResult());
    applicationDTO.setNote(entity.getNote());
    //applicationDTO.setCreateTime(entity.getCreateTime());

    if (entity.getCandidateEntity() != null) {
      applicationDTO.setCandidateId(entity.getCandidateEntity().getId());
    }

    if (entity.getJobPostEntity() != null) {
      applicationDTO.setJobPostId(entity.getJobPostEntity().getId());
    }

    return applicationDTO;
  }

  public ApplicationEntity toEntity(ApplicationDto dto) {
    ApplicationEntity entity = new ApplicationEntity();
    entity.setId(dto.getId());
    entity.setResult(dto.getResult());
    entity.setNote(dto.getNote());
    //entity.setCreateTime(dto.getCreateTime());
    //      try {
    //        Optional<CandidateEntity> candidate = candidateRepository.findById(dto.getId());
    //        if (candidate.isPresent()) {
    //          entity.setCandidateEntity(candidate.get());
    //        } else {
    //          throw new CustomException("Can't convert Candidate in Application");
    //        }
    //      } catch (Exception e) {
    //        throw new CustomException("Can't convert Candidate in Application");
    //      }

    // entity.setCandidateEntity(dto.getCandidateId());
    return entity;
  }

  // Candidate

  public CandidateDto toDTO(CandidateEntity entity) {
    CandidateDto dto = new CandidateDto();
    dto.setId(entity.getId());
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setEmailContact(entity.getEmailContact());
    dto.setPhoneNum(entity.getPhoneNum());
    dto.setIntroduction(entity.getIntroduction());
    dto.setEducationLevel(entity.getEducationLevel());
    dto.setWorkStatus(entity.getWorkStatus());
    dto.setBlind(entity.isBlind());
    dto.setDeaf(entity.isDeaf());
    dto.setCommunicationDis(entity.isCommunicationDis());
    dto.setHandDis(entity.isHandDis());
    dto.setLabor(entity.isLabor());
    dto.setDetailDis(entity.getDetailDis());
    dto.setVerifiedDis(entity.isVerifiedDis());
    dto.setSkills(entity.getSkills());
    dto.setServices(entity.getServices());
    dto.setPosition(entity.getPosition());

    if (entity.getUserEntity() == null) {
      throw new CustomException("Can't convert userEntity because it is null");
    }
    dto.setUserId(entity.getUserEntity().getId());

    return dto;
  }

  public CandidateEntity toEntity(CandidateDto dto) {
    CandidateEntity entity = new CandidateEntity();

    entity.setId(dto.getId());
    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
    entity.setEmailContact(dto.getEmailContact());
    entity.setPhoneNum(dto.getPhoneNum());
    entity.setIntroduction(dto.getIntroduction());
    entity.setEducationLevel(dto.getEducationLevel());
    entity.setWorkStatus(dto.getWorkStatus());
    entity.setBlind(dto.isBlind());
    entity.setDeaf(dto.isDeaf());
    entity.setCommunicationDis(dto.isCommunicationDis());
    entity.setHandDis(dto.isHandDis());
    entity.setLabor(dto.isLabor());
    entity.setDetailDis(dto.getDetailDis());
    entity.setVerifiedDis(dto.isVerifiedDis());
    entity.setSkills(dto.getSkills());
    entity.setServices(dto.getServices());
    entity.setPosition(dto.getPosition());

    Optional<UserEntity> userEntity = userRepository.findById(dto.getUserId());
    if (userEntity.isEmpty()) {
      throw new NoSuchElementException("Can't convert userId");
    }
    entity.setUserEntity(userEntity.get());

    return entity;
  }

  // Company
  public CompanyDto toDTO(CompanyEntity entity) {
    CompanyDto dto =
    CompanyDto.builder().id(entity.getId()).name(entity.getName()).image(entity.getImage()).description(entity.getDescription()).website(entity.getWebsite())
                    .isActive(entity.isActive()).build();

    if (!Objects.isNull(entity.getCreatedEmployer())) {
      dto.setCreatedEmployerId(entity.getCreatedEmployer().getId());
    }

    return dto;
  }

  public CompanyEntity toEntity(CompanyDto dto) {
    CompanyEntity entity = new CompanyEntity();
    entity.setId(dto.getId());
    entity.setName(dto.getName());
    entity.setImage(dto.getImage());
    entity.setDescription(dto.getDescription());
    entity.setWebsite(dto.getWebsite());
    entity.setActive(dto.isActive());

    if (dto.getCreatedEmployerId() != null) {
      Optional<EmployerEntity> employerEntity =
          employerRepository.findById(dto.getCreatedEmployerId());
      if (employerEntity.isEmpty()) {
        throw new NoSuchElementException("Can't convert userId");
      }
      entity.setCreatedEmployer(employerEntity.get());
    }

    return entity;
  }

  // Document
  public DocumentDto toDTO(DocumentEntity entity) {
    DocumentDto dto = new DocumentDto();
    dto.setId(entity.getId());
    dto.setType(entity.getType());
    dto.setTitle(entity.getTitle());
    dto.setUrl(entity.getUrl());

    return dto;
  }

  public DocumentEntity toEntity(DocumentDto dto) {
    DocumentEntity entity = new DocumentEntity();
    entity.setId(dto.getId());
    entity.setType(dto.getType());
    entity.setTitle(dto.getTitle());
    entity.setUrl(dto.getUrl());

    return entity;
  }

  // Employer
  public EmployerDto toDTO(EmployerEntity entity) {
    EmployerDto dto = new EmployerDto();
    dto.setId(entity.getId());
    dto.setFirstname(entity.getFirstname());
    dto.setLastname(entity.getLastname());
    dto.setRecruitmentEmail(entity.getRecruitmentEmail());
    dto.setRecruitmentPhone(entity.getRecruitmentPhone());

    if (entity.getUserEntity() == null) {
      throw new CustomException("Can't convert userEntity because it is null");
    }
    dto.setUserId(entity.getUserEntity().getId());
    return dto;
  }

  public EmployerEntity toEntity(EmployerDto dto) {
    EmployerEntity entity = new EmployerEntity();
    entity.setId(dto.getId());
    entity.setFirstname(dto.getFirstname());
    entity.setLastname(dto.getLastname());
    entity.setRecruitmentEmail(dto.getRecruitmentEmail());
    entity.setRecruitmentPhone(dto.getRecruitmentPhone());

    Optional<UserEntity> userEntity = userRepository.findById(dto.getUserId());
    if (userEntity.isEmpty()) {
      throw new NoSuchElementException("Can't convert userId");
    }
    entity.setUserEntity(userEntity.get());

    return entity;
  }

  // Evaluate
  public EvaluateDto toDTO(EvaluateEntity entity) {
    EvaluateDto dto = new EvaluateDto();
    dto.setId(entity.getId());
    dto.setRate(entity.getRate());
    dto.setNote(entity.getNote());

    // candidateId and EmployerId
    return dto;
  }

  public EvaluateEntity toEntity(EvaluateDto dto) {
    EvaluateEntity entity = new EvaluateEntity();
    entity.setId(dto.getId());
    entity.setRate(dto.getRate());
    entity.setNote(dto.getNote());

    // continue
    return entity;
  }

  // Experience
  public ExperienceDto toDTO(ExperienceEntity entity) {
    ExperienceDto dto = new ExperienceDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setEmploymentType(entity.getEmploymentType());
    dto.setCompany(entity.getCompany());
    dto.setWorking(entity.isWorking());
    dto.setIndustry(entity.getIndustry());
    dto.setStartDate(entity.getStartDate());
    dto.setEndDate(entity.getEndDate());
    dto.setDescription(entity.getDescription());
    dto.setSkills(entity.getSkills());

    if (entity.getCandidateEntity() == null) {
      throw new CustomException("Can't convert candidateEntity because it is null");
    }
    dto.setCandidateId(entity.getCandidateEntity().getId());

    return dto;
  }

  public ExperienceEntity toEntity(ExperienceDto dto) {
    ExperienceEntity entity = new ExperienceEntity();
    entity.setId(dto.getId());
    entity.setTitle(dto.getTitle());
    entity.setEmploymentType(dto.getEmploymentType());
    entity.setCompany(dto.getCompany());
    entity.setWorking(dto.isWorking());
    entity.setIndustry(dto.getIndustry());
    entity.setStartDate(dto.getStartDate());
    entity.setEndDate(dto.getEndDate());
    entity.setDescription(dto.getDescription());
    entity.setSkills(dto.getSkills());

    Optional<CandidateEntity> candidateEntity = candidateRepository.findById(dto.getCandidateId());
    if (candidateEntity.isPresent()) {
      // throw new NoSuchElementException("Can't convert candidateId");
      entity.setCandidateEntity(candidateEntity.get());
    }

    return entity;
  }

  // Image
  public StorageFileDto toDTO(FileEntity entity) {
    StorageFileDto dto = new StorageFileDto();
    dto.setId(entity.getId());
    dto.setType(entity.getType());
    dto.setName(entity.getName());
    dto.setUrl(entity.getUrl());
    dto.setObjectId(entity.getObjectId());
    //dto.setAuthor(entity.getCreated_by());
    dto.setCloudinaryPublicId(entity.getCloudinaryPublicId());
    dto.setCreateAt(entity.getCreatedAt());

    return dto;
  }

  public FileEntity toEntity(StorageFileDto dto) {
    FileEntity entity = new FileEntity();
    entity.setId(dto.getId());
    entity.setType(dto.getType());
    entity.setName(dto.getName());
    entity.setUrl(dto.getUrl());
    entity.setObjectId(dto.getObjectId());
    //entity.setCreated_by(dto.getAuthor());
    entity.setCloudinaryPublicId(dto.getCloudinaryPublicId());
    entity.setCreatedAt(dto.getCreateAt());

    return entity;
  }

  // JobAlert
  public JobAlertDto toDTO(JobAlertEntity entity) {
    JobAlertDto dto = new JobAlertDto();
    dto.setId(entity.getId());
    dto.setTag(entity.getTag());
    dto.setIndustry(entity.getIndustry());
    dto.setEmploymentType(entity.getEmploymentType());
    dto.setWorkplaceType(entity.getWorkplaceType());
    dto.setCity(entity.getCity());
    dto.setMinBudget(entity.getMinBudget());

    if (entity.getCandidateEntity() == null) {
      throw new CustomException("Can't convert candidateEntity because it is null");
    }
    dto.setCandidateId(entity.getCandidateEntity().getId());

    return dto;
  }

  public JobAlertEntity toEntity(JobAlertDto dto) {
    JobAlertEntity entity = new JobAlertEntity();
    entity.setId(dto.getId());
    entity.setTag(dto.getTag());
    entity.setIndustry(dto.getIndustry());
    entity.setEmploymentType(dto.getEmploymentType());
    entity.setWorkplaceType(dto.getWorkplaceType());
    entity.setCity(dto.getCity());
    entity.setMinBudget(dto.getMinBudget());

    Optional<CandidateEntity> candidateEntity = candidateRepository.findById(dto.getCandidateId());
    if (candidateEntity.isPresent()) {
      // throw new NoSuchElementException("Can't convert candidateId");
      entity.setCandidateEntity(candidateEntity.get());
    }

    return entity;
  }

  // JobPost
  public JobPostDto toDTO(JobPostEntity entity) {
    JobPostDto dto = new JobPostDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setEmploymentType(entity.getEmploymentType());
    dto.setWorkplaceType(entity.getWorkplaceType());
    dto.setDescription(entity.getDescription());
    dto.setCity(entity.getCity());
    dto.setAddress(entity.getAddress());
    dto.setEducationLevel(entity.getEducationLevel());
    dto.setExperienceYear(entity.getExperienceYear());
    dto.setQuantity(entity.getQuantity());
    dto.setMinBudget(entity.getMinBudget());
    dto.setMaxBudget(entity.getMaxBudget());
    dto.setCreateTime(entity.getCreateTime());
    dto.setDueTime(entity.getDueTime());
    dto.setWorkStatus(entity.getWorkStatus());
    dto.setBlind(entity.isBlind());
    dto.setDeaf(entity.isDeaf());
    dto.setCommunicationDis(entity.isCommunicationDis());
    dto.setHandDis(entity.isHandDis());
    dto.setLabor(entity.isLabor());
    dto.setSkills(entity.getSkills());
    dto.setPositions(entity.getPositions());
    dto.setViews(entity.getViews());
    dto.setActive(entity.isActive());
    dto.setDeleted(entity.isDeleted());

    if (entity.getCompanyEntity()!= null) {
      dto.setLogo(entity.getCompanyEntity().getImage());
      dto.setCompanyId(entity.getCompanyEntity().getId());
    }

    if (entity.getCreatedEmployer() == null) {
      throw new CustomException("Can't convert createEntity because it is null");
    }
    dto.setCreatedEmployerId(entity.getCreatedEmployer().getId());

    return dto;
  }

  public JobPostEntity toEntity(JobPostDto dto) {
    JobPostEntity entity = new JobPostEntity();
    entity.setId(dto.getId());
    entity.setTitle(dto.getTitle());
    entity.setEmploymentType(dto.getEmploymentType());
    entity.setWorkplaceType(dto.getWorkplaceType());
    entity.setDescription(dto.getDescription());
    entity.setCity(dto.getCity());
    entity.setAddress(dto.getAddress());
    entity.setEducationLevel(dto.getEducationLevel());
    entity.setExperienceYear(dto.getExperienceYear());
    entity.setQuantity(dto.getQuantity());
    entity.setMinBudget(dto.getMinBudget());
    entity.setMaxBudget(dto.getMaxBudget());
    entity.setCreateTime(dto.getCreateTime());
    entity.setDueTime(dto.getDueTime());
    entity.setWorkStatus(dto.getWorkStatus());
    entity.setBlind(dto.isBlind());
    entity.setDeaf(dto.isDeaf());
    entity.setCommunicationDis(dto.isCommunicationDis());
    entity.setHandDis(dto.isHandDis());
    entity.setLabor(dto.isLabor());
    entity.setSkills(dto.getSkills());
    entity.setViews(dto.getViews());
    entity.setPositions(dto.getPositions());
    entity.setActive(dto.isActive());

    Optional<EmployerEntity> createEmployer =
        employerRepository.findById(dto.getCreatedEmployerId());
    if (createEmployer.isEmpty()) {
      throw new NoSuchElementException("Can't convert createEmployerId");
    }
    entity.setCreatedEmployer(createEmployer.get());
    System.out.println("category Id = "+dto.getCategoryId());
    Optional<CategoryEntity> categoryEntity =
            categoryRepository.findById(dto.getCategoryId());

    if (categoryEntity.isEmpty()) {
      throw new NoSuchElementException("Can't convert categoryId");
    }
    System.out.println("category  = "+categoryEntity.get().getName());
    entity.setCategoryEntity(categoryEntity.get());

    if (dto.getCompanyId() != -1) {
      Optional<CompanyEntity> company =
              companyRepository.findById(dto.getCompanyId());
      if (company.isPresent()) {
        entity.setCompanyEntity(company.get());
      }
    }

    return entity;
  }

  // Notification
  public NotificationDto toDTO(NotificationEntity entity) {
    NotificationDto dto = new NotificationDto();
    dto.setId(entity.getId());
    dto.setType(entity.getType());
    dto.setTitle(entity.getTitle());
    dto.setBrief(entity.getBrief());
    dto.setTime(entity.getTime());

    return dto;
  }

  public NotificationEntity toEntity(NotificationDto dto) {
    NotificationEntity entity = new NotificationEntity();
    entity.setId(dto.getId());
    entity.setType(dto.getType());
    entity.setTitle(dto.getTitle());
    entity.setBrief(dto.getBrief());
    entity.setTime(dto.getTime());

    return entity;
  }

  public ExtraInfoDto toDTO(ExtraInfoEntity entity) {
    ExtraInfoDto dto = new ExtraInfoDto();
    dto.setName(entity.getName());
    dto.setType(entity.getType());
    dto.setActive(entity.isActive());

    return dto;
  }

  public ExtraInfoEntity toEntity(ExtraInfoDto dto) {
    ExtraInfoEntity entity = new ExtraInfoEntity();
    entity.setName(dto.getName());
    entity.setType(dto.getType());
    entity.setActive(dto.isActive());

    return entity;
  }

  public InvoiceDto toDTO(InvoiceEntity entity) {
    InvoiceDto dto = new InvoiceDto();
    dto.setId(entity.getId());
    dto.setEmail(entity.getEmail());
    dto.setPhone(entity.getPhone());
    dto.setServiceType(entity.getServiceType());
    dto.setPrice(entity.getPrice());
    dto.setTransactionCode(entity.getTransactionCode());
    dto.setPayTime(entity.getPayTime());
    dto.setPaymentMethod(entity.getPaymentMethod());
    dto.setStatus(entity.getStatus());

    return dto;
  }

  public InvoiceEntity toEntity(InvoiceDto dto) {
    InvoiceEntity entity = new InvoiceEntity();
    entity.setId(dto.getId());
    entity.setEmail(dto.getEmail());
    entity.setPhone(dto.getPhone());
    entity.setServiceType(dto.getServiceType());
    entity.setPrice(dto.getPrice());
    entity.setTransactionCode(dto.getTransactionCode());
    entity.setPayTime(dto.getPayTime());
    entity.setPaymentMethod(dto.getPaymentMethod());
    entity.setStatus(dto.getStatus());

    return entity;
  }

  public PackageDto toDTO(PackageEntity entity) {
    PackageDto dto = new PackageDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setCode(entity.getCode());
    dto.setPrice(entity.getPrice());
    dto.setCost(entity.getCost());
    dto.setDuration(entity.getDuration());
    dto.setNumOfJobPost(entity.getNumOfJobPost());
    dto.setDescription(entity.getDescription());
    dto.setServiceType(entity.getServiceType());
    dto.setPublicTime(entity.getPublicTime());
    dto.setForUserType(entity.getForUserType());

    return dto;
  }

  public PackageEntity toEntity(PackageDto dto) {
    PackageEntity entity = new PackageEntity();
    entity.setId(dto.getId());
    entity.setName(dto.getName());
    entity.setCode(dto.getCode());
    entity.setPrice(dto.getPrice());
    entity.setCost(dto.getCost());
    entity.setDuration(dto.getDuration());
    entity.setNumOfJobPost(dto.getNumOfJobPost());
    entity.setDescription(dto.getDescription());
    entity.setServiceType(dto.getServiceType());
    entity.setPublicTime(dto.getPublicTime());
    entity.setForUserType(dto.getForUserType());

    return entity;
  }

  public SubscribeDto toDTO(SubscribeEntity entity) {
    SubscribeDto dto = new SubscribeDto();
    dto.setId(entity.getId());
    dto.setStartTime(entity.getStartTime());
    dto.setExpirationTime(entity.getExpirationTime());
    dto.setTransactionCode(entity.getPaymentTransactionCode());
    return dto;
  }

  public SubscribeEntity toEntity(SubscribeDto dto) {
    SubscribeEntity entity = new SubscribeEntity();
    entity.setId(dto.getId());
    entity.setStartTime(dto.getStartTime());
    entity.setExpirationTime(dto.getExpirationTime());
    entity.setPaymentTransactionCode(dto.getTransactionCode());
    return entity;
  }

  public CategoryDto toDTO(CategoryEntity entity) {
    CategoryDto dto = new CategoryDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setActive(entity.isActive());
    return dto;
  }

  public CategoryEntity toEntity(CategoryDto dto) {
    CategoryEntity entity = new CategoryEntity();
    entity.setId(dto.getId());
    entity.setName(dto.getName());
    entity.setActive(dto.isActive());
    return entity;
  }

  public BlogPostDto toDTO(BlogPostEntity entity) {
//    // setup
//    TypeMap<BlogPostEntity, BlogPostDto> propertyMapper = modelMapper.createTypeMap(BlogPostEntity.class, BlogPostDto.class);
//    // add deep mapping to flatten source's Player object into a single field in destination
//    propertyMapper.addMappings(
//            mapper -> mapper.map(src -> src.getUserEntity().getId(), BlogPostDto::setUserId)
//    );
    BlogPostDto blogPostDTO = modelMapper.map(entity, BlogPostDto.class);
    return blogPostDTO;
  }

  public BlogPostEntity toEntity(BlogPostDto dto) {
    BlogPostEntity entity = modelMapper.map(dto, BlogPostEntity.class);

    try {
      Optional<UserEntity> userEntity = userRepository.findById(dto.getUserId());
      if (userEntity.isPresent()) {
        entity.setAuthor(userEntity.get());
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return entity;
  }

  public CommentDto toDTO(CommentEntity entity) {
    CommentDto dto = modelMapper.map(entity, CommentDto.class);
    return dto;
  }

  public CommentEntity toEntity(CommentDto dto) {
    CommentEntity entity = modelMapper.map(dto, CommentEntity.class);
    try {
      Optional<BlogPostEntity> blogPostEntity = blogPostRepository.findById(dto.getBlogPostId());
      if (blogPostEntity.isPresent()) {
        entity.setBlogPostEntity(blogPostEntity.get());
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return entity;
  }

  public SubCommentDto toDTO(SubCommentEntity entity) {
    SubCommentDto dto = modelMapper.map(entity, SubCommentDto.class);
    return dto;
  }

  public SubCommentEntity toEntity(SubCommentDto dto) {
    SubCommentEntity entity = modelMapper.map(dto, SubCommentEntity.class);
    try {
      Optional<CommentEntity> commentEntity = commentRepository.findById(dto.getCommentId());
      if (commentEntity.isPresent()) {
        entity.setCommentEntity(commentEntity.get());
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return entity;
  }
}
