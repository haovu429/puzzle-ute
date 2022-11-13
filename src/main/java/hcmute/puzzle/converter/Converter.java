package hcmute.puzzle.converter;

import hcmute.puzzle.dto.*;
import hcmute.puzzle.entities.*;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Converter {

  @Autowired private ApplicationRepository applicationRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired private CandidateRepository candidateRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private DocumentRepository documentRepository;
  @Autowired private EmployerRepository employerRepository;
  @Autowired private EvaluateRepository evaluateRepository;
  @Autowired private ExperienceRepository experienceRepository;
  @Autowired private ImageRepository imageRepository;
  @Autowired private JobAlertRepository jobAlertRepository;
  @Autowired private JobPostRepository jobPostRepository;
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private ServiceRepository serviceRepository;
  @Autowired private SkillRepository skillRepository;
  @Autowired private UserRepository userRepository;

  // User
  public UserDTO toDTO(UserEntity entity) {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(entity.getId());
    userDTO.setUsername(entity.getUsername());
    userDTO.setEmail(entity.getEmail());
    userDTO.setPassword(entity.getPassword());
    userDTO.setPhone(entity.getPhone());
    userDTO.setAvatar(entity.getAvatar());
    userDTO.setOnline(entity.isOnline());
    userDTO.setJoinDate(entity.getJoinDate());
    userDTO.setLastOnline(entity.getLastOnline());
    userDTO.setActive(entity.isActive());

    if (!entity.getRoles().isEmpty()) {
      userDTO.setRoleCodes(
          entity.getRoles().stream().map(role -> role.getCode()).collect(Collectors.toSet()));
    }

    return userDTO;
  }

  public UserEntity toEntity(UserDTO dto) {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(dto.getId());
    userEntity.setUsername(dto.getUsername());
    userEntity.setEmail(dto.getEmail());
    userEntity.setPassword(dto.getPassword());
    userEntity.setPhone(dto.getPhone());
    userEntity.setAvatar(dto.getAvatar());
    userEntity.setOnline(dto.isOnline());
    userEntity.setJoinDate(dto.getJoinDate());
    userEntity.setLastOnline(dto.getLastOnline());
    userEntity.setActive(dto.isActive());
    //        Set<RoleEntity> roleEntities = dto.getRoles().stream().map(role -> toEntity(role))
    //                .collect(Collectors.toSet());

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
  public RoleDTO toDTO(RoleEntity entity) {
    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setCode(entity.getCode());
    roleDTO.setName(entity.getName());
    return roleDTO;
  }

  public RoleEntity toEntity(RoleDTO dto) {
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setCode(dto.getCode());
    roleEntity.setName(dto.getName());
    return roleEntity;
  }

  // ApplicationEntity
  public ApplicationDTO toDTO(ApplicationEntity entity) {
    ApplicationDTO applicationDTO = new ApplicationDTO();
    applicationDTO.setId(entity.getId());
    applicationDTO.setResult(entity.getResult());
    applicationDTO.setNote(entity.getNote());

    if (entity.getCandidateEntity() != null) {
      applicationDTO.setCandidateId(entity.getId());
    }

    if (entity.getJobPostEntity() != null) {
      applicationDTO.setJobPostId(entity.getJobPostEntity().getId());
    }

    return applicationDTO;
  }

  public ApplicationEntity toEntity(ApplicationDTO dto) {
    ApplicationEntity entity = new ApplicationEntity();
    entity.setId(dto.getId());
    entity.setResult(dto.getResult());
    entity.setNote(dto.getNote());
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

  public CandidateDTO toDTO(CandidateEntity entity) {
    CandidateDTO dto = new CandidateDTO();
    dto.setId(entity.getId());
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
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

  public CandidateEntity toEntity(CandidateDTO dto) {
    CandidateEntity entity = new CandidateEntity();

    entity.setId(dto.getId());
    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
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
  public CompanyDTO toDTO(CompanyEntity entity) {
    CompanyDTO dto = new CompanyDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setDescription(entity.getDescription());
    dto.setWebsite(entity.getWebsite());
    dto.setActive(entity.isActive());

    if (entity.getCreatedEmployer() != null) {
      dto.setCreatedEmployerId(entity.getCreatedEmployer().getId());
    }

    return dto;
  }

  public CompanyEntity toEntity(CompanyDTO dto) {
    CompanyEntity entity = new CompanyEntity();
    entity.setId(dto.getId());
    entity.setName(dto.getName());
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
  public DocumentDTO toDTO(DocumentEntity entity) {
    DocumentDTO dto = new DocumentDTO();
    dto.setId(entity.getId());
    dto.setType(entity.getType());
    dto.setTitle(entity.getTitle());
    dto.setUrl(entity.getUrl());

    return dto;
  }

  public DocumentEntity toEntity(DocumentDTO dto) {
    DocumentEntity entity = new DocumentEntity();
    entity.setId(dto.getId());
    entity.setType(dto.getType());
    entity.setTitle(dto.getTitle());
    entity.setUrl(dto.getUrl());

    return entity;
  }

  // Employer
  public EmployerDTO toDTO(EmployerEntity entity) {
    EmployerDTO dto = new EmployerDTO();
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

  public EmployerEntity toEntity(EmployerDTO dto) {
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
  public EvaluateDTO toDTO(EvaluateEntity entity) {
    EvaluateDTO dto = new EvaluateDTO();
    dto.setId(entity.getId());
    dto.setRate(entity.getRate());
    dto.setNote(entity.getNote());

    // candidateId and EmployerId
    return dto;
  }

  public EvaluateEntity toEntity(EvaluateDTO dto) {
    EvaluateEntity entity = new EvaluateEntity();
    entity.setId(dto.getId());
    entity.setRate(dto.getRate());
    entity.setNote(dto.getNote());

    // continue
    return entity;
  }

  // Experience
  public ExperienceDTO toDTO(ExperienceEntity entity) {
    ExperienceDTO dto = new ExperienceDTO();
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

  public ExperienceEntity toEntity(ExperienceDTO dto) {
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
  public ImageDTO toDTO(ImageEntity entity) {
    ImageDTO dto = new ImageDTO();
    dto.setId(entity.getId());
    dto.setType(entity.getType());
    dto.setTitle(entity.getTitle());
    dto.setUrl(entity.getUrl());
    dto.setCodeId(entity.getCodeId());

    return dto;
  }

  public ImageEntity toEntity(ImageDTO dto) {
    ImageEntity entity = new ImageEntity();
    entity.setId(dto.getId());
    entity.setType(dto.getType());
    entity.setTitle(dto.getTitle());
    entity.setUrl(dto.getUrl());
    entity.setCodeId(dto.getCodeId());

    return entity;
  }

  // JobAlert
  public JobAlertDTO toDTO(JobAlertEntity entity) {
    JobAlertDTO dto = new JobAlertDTO();
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

  public JobAlertEntity toEntity(JobAlertDTO dto) {
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
  public JobPostDTO toDTO(JobPostEntity entity) {
    JobPostDTO dto = new JobPostDTO();
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
    dto.setDueTime(entity.getDueTime());
    dto.setWorkStatus(entity.getWorkStatus());
    dto.setBlind(entity.isBlind());
    dto.setDeaf(entity.isDeaf());
    dto.setCommunicationDis(entity.isCommunicationDis());
    dto.setHandDis(entity.isHandDis());
    dto.setLabor(entity.isLabor());
    dto.setSkills(entity.getSkills());
    dto.setActive(entity.isActive());

    if (entity.getCreatedEmployer() == null) {
      throw new CustomException("Can't convert createEntity because it is null");
    }
    dto.setCreatedEmployerId(entity.getCreatedEmployer().getId());

    return dto;
  }

  public JobPostEntity toEntity(JobPostDTO dto) {
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
    entity.setDueTime(dto.getDueTime());
    entity.setWorkStatus(dto.getWorkStatus());
    entity.setBlind(dto.isBlind());
    entity.setDeaf(dto.isDeaf());
    entity.setCommunicationDis(dto.isCommunicationDis());
    entity.setHandDis(dto.isHandDis());
    entity.setLabor(dto.isLabor());
    entity.setSkills(dto.getSkills());
    entity.setActive(dto.isActive());

    Optional<EmployerEntity> createEmployer =
        employerRepository.findById(dto.getCreatedEmployerId());
    if (createEmployer.isEmpty()) {
      throw new NoSuchElementException("Can't convert createEmployerId");
    }
    entity.setCreatedEmployer(createEmployer.get());

    return entity;
  }

  // Notification
  public NotificationDTO toDTO(NotificationEntity entity) {
    NotificationDTO dto = new NotificationDTO();
    dto.setId(entity.getId());
    dto.setType(entity.getType());
    dto.setTitle(entity.getTitle());
    dto.setBrief(entity.getBrief());
    dto.setTime(entity.getTime());

    return dto;
  }

  public NotificationEntity toEntity(NotificationDTO dto) {
    NotificationEntity entity = new NotificationEntity();
    entity.setId(dto.getId());
    entity.setType(dto.getType());
    entity.setTitle(dto.getTitle());
    entity.setBrief(dto.getBrief());
    entity.setTime(dto.getTime());

    return entity;
  }

//  // Service
//  public ServiceDTO toDTO(ServiceEntity entity) {
//    ServiceDTO dto = new ServiceDTO();
//    dto.setId(entity.getId());
//    dto.setName(entity.getName());
//    dto.setActive(entity.isActive());
//
//    return dto;
//  }
//
//  public ServiceEntity toEntity(ServiceDTO dto) {
//    ServiceEntity entity = new ServiceEntity();
//    entity.setId(dto.getId());
//    entity.setName(dto.getName());
//    entity.setActive(dto.isActive());
//
//    return entity;
//  }
//
//  // Skill
//  public SkillDTO toDTO(SkillEntity entity) {
//    SkillDTO dto = new SkillDTO();
//    dto.setId(entity.getId());
//    dto.setName(entity.getName());
//    dto.setActive(entity.isActive());
//
//    return dto;
//  }
//
//  public SkillEntity toEntity(SkillDTO dto) {
//    SkillEntity entity = new SkillEntity();
//    entity.setId(dto.getId());
//    entity.setName(dto.getName());
//    entity.setActive(dto.isActive());
//
//    return entity;
//  }
//
//  // Position
//  public PositionDTO toDTO(PositionEntity entity) {
//    PositionDTO dto = new PositionDTO();
//    dto.setId(entity.getId());
//    dto.setName(entity.getName());
//    dto.setActive(entity.isActive());
//
//    return dto;
//  }
//
//  public PositionEntity toEntity(PositionDTO dto) {
//    PositionEntity entity = new PositionEntity();
//    entity.setId(dto.getId());
//    entity.setName(dto.getName());
//    entity.setActive(dto.isActive());
//
//    return entity;
//  }

  public ExtraInfoDTO toDTO(ExtraInfoEntity entity) {
    ExtraInfoDTO dto = new ExtraInfoDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setType(entity.getType());
    dto.setActive(entity.isActive());

    return dto;
  }

  public ExtraInfoEntity toEntity(ExtraInfoDTO dto) {
    ExtraInfoEntity entity = new ExtraInfoEntity();
    entity.setId(dto.getId());
    entity.setName(dto.getName());
    entity.setType(dto.getType());
    entity.setActive(dto.isActive());

    return entity;
  }
}
