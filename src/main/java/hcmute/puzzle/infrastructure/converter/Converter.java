package hcmute.puzzle.infrastructure.converter;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.*;
import hcmute.puzzle.infrastructure.entities.Package;
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
public class Converter<T, D> {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;
    @Autowired
    private CommentRepository commentRepository;

    public Converter() {
    }


    // User
    public UserPostDto toDTO(User entity) {
        UserPostDto userPostDTO = new UserPostDto();
        userPostDTO.setId(entity.getId());
        userPostDTO.setUsername(entity.getUsername());
        userPostDTO.setEmail(entity.getEmail());
        userPostDTO.setPhone(entity.getPhone());
        userPostDTO.setAvatar(entity.getAvatar());
        // userPostDTO.setJoinDate(entity.getCreatedAt());
        userPostDTO.setLastLoginAt(entity.getLastLoginAt());
        userPostDTO.setIsActive(entity.getIsActive());
        if (entity.getProvider() != null) {
            userPostDTO.setProvider(entity.getProvider().toString());
        }
        userPostDTO.setFullName(entity.getFullName());
        userPostDTO.setEmailVerified(entity.getEmailVerified());
        userPostDTO.setLocale(entity.getLocale());

        if (!entity.getRoles().isEmpty()) {
            userPostDTO.setRoleCodes(
                    entity.getRoles().stream().map(role -> role.getCode()).collect(Collectors.toSet()).stream().collect(Collectors.toList()));
        }

        return userPostDTO;
    }

    public User toEntity(UserPostDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setLastLoginAt(dto.getLastLoginAt());
        user.setIsActive(dto.getIsActive());
        user.setProvider(Provider.asProvider(dto.getProvider()));
        user.setFullName(dto.getFullName());
        user.setEmailVerified(dto.getEmailVerified());
        user.setLocale(dto.getLocale());

        Set<Role> roleEntities = new HashSet<>();
        for (String code : dto.getRoleCodes()) {
            if (roleRepository.existsById(code)) {
                roleEntities.add(roleRepository.findOneByCode(code));
            } else {
                throw new CustomException("Can't convert! Not found role has role code = " + code);
            }
        }
        user.setRoles(roleEntities);

        return user;
    }

    //  Role
    public RoleDto toDTO(Role entity) {
        RoleDto roleDTO = RoleDto.builder().code(entity.getCode()).name(entity.getName()).build();
        return roleDTO;
    }

    public Role toEntity(RoleDto dto) {
        Role role = new Role();
        role.setCode(dto.getCode());
        role.setName(dto.getName());
        return role;
    }

    // ApplicationEntity
    public ApplicationDto toDTO(Application entity) {
        ApplicationDto applicationDTO = new ApplicationDto();
        applicationDTO.setId(entity.getId());
        applicationDTO.setResult(entity.getResult());
        applicationDTO.setNote(entity.getNote());
        //applicationDTO.setCreateTime(entity.getCreateTime());

        if (entity.getCandidate() != null) {
            applicationDTO.setCandidateId(entity.getCandidate().getId());
        }

        if (entity.getJobPost() != null) {
            applicationDTO.setJobPostId(entity.getJobPost().getId());
        }

        return applicationDTO;
    }

    public Application toEntity(ApplicationDto dto) {
        Application entity = new Application();
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

    public CandidateDto toDTO(Candidate entity) {
        CandidateDto dto = new CandidateDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmailContact(entity.getEmailContact());
        dto.setPhoneNum(entity.getPhoneNum());
        dto.setIntroduction(entity.getIntroduction());
        dto.setEducationLevel(entity.getEducationLevel());
        dto.setWorkStatus(entity.getWorkStatus());
        dto.setBlind(entity.getBlind());
        dto.setDeaf(entity.getDeaf());
        dto.setCommunicationDis(entity.getCommunicationDis());
        dto.setHandDis(entity.getHandDis());
        dto.setLabor(entity.getLabor());
        dto.setDetailDis(entity.getDetailDis());
        dto.setVerifiedDis(entity.getVerifiedDis());
        dto.setSkills(entity.getSkills());
        dto.setServices(entity.getServices());
        dto.setPosition(entity.getPosition());

        if (entity.getUser() == null) {
            throw new CustomException("Can't convert userEntity because it is null");
        }
        dto.setUserId(entity.getUser().getId());

        return dto;
    }

    public Candidate toEntity(CandidateDto dto) {
        Candidate entity = new Candidate();

        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmailContact(dto.getEmailContact());
        entity.setPhoneNum(dto.getPhoneNum());
        entity.setIntroduction(dto.getIntroduction());
        entity.setEducationLevel(dto.getEducationLevel());
        entity.setWorkStatus(dto.getWorkStatus());
        entity.setBlind(dto.getBlind());
        entity.setDeaf(dto.getDeaf());
        entity.setCommunicationDis(dto.getCommunicationDis());
        entity.setHandDis(dto.getHandDis());
        entity.setLabor(dto.getLabor());
        entity.setDetailDis(dto.getDetailDis());
        entity.setVerifiedDis(dto.getVerifiedDis());
        entity.setSkills(dto.getSkills());
        entity.setServices(dto.getServices());
        entity.setPosition(dto.getPosition());

        Optional<User> userEntity = userRepository.findById(dto.getUserId());
        if (userEntity.isEmpty()) {
            throw new NoSuchElementException("Can't convert userId");
        }
        entity.setUser(userEntity.get());

        return entity;
    }

    // Company
    public CompanyDto toDTO(Company entity) {
        CompanyDto dto =
                CompanyDto.builder().id(entity.getId()).name(entity.getName()).image(entity.getImage()).description(entity.getDescription()).website(entity.getWebsite())
                        .isActive(entity.getIsActive()).build();

        if (!Objects.isNull(entity.getCreatedEmployer())) {
            dto.setCreatedEmployerId(entity.getCreatedEmployer().getId());
        }

        return dto;
    }

    public Company toEntity(CompanyDto dto) {
        Company entity = new Company();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setImage(dto.getImage());
        entity.setDescription(dto.getDescription());
        entity.setWebsite(dto.getWebsite());
        entity.setIsActive(dto.getIsActive());

        if (dto.getCreatedEmployerId() != null) {
            Optional<Employer> employerEntity =
                    employerRepository.findById(dto.getCreatedEmployerId());
            if (employerEntity.isEmpty()) {
                throw new NoSuchElementException("Can't convert userId");
            }
            entity.setCreatedEmployer(employerEntity.get());
        }

        return entity;
    }

    // Document
    public DocumentDto toDTO(Document entity) {
        DocumentDto dto = new DocumentDto();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setTitle(entity.getTitle());
        dto.setUrl(entity.getUrl());

        return dto;
    }

    public Document toEntity(DocumentDto dto) {
        Document entity = new Document();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setTitle(dto.getTitle());
        entity.setUrl(dto.getUrl());

        return entity;
    }

    // Employer
    public EmployerDto toDTO(Employer entity) {
        EmployerDto dto = new EmployerDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setRecruitmentEmail(entity.getRecruitmentEmail());
        dto.setRecruitmentPhone(entity.getRecruitmentPhone());

        if (entity.getUser() == null) {
            throw new CustomException("Can't convert userEntity because it is null");
        }
        dto.setUserId(entity.getUser().getId());
        return dto;
    }

    public Employer toEntity(EmployerDto dto) {
        Employer entity = new Employer();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setRecruitmentEmail(dto.getRecruitmentEmail());
        entity.setRecruitmentPhone(dto.getRecruitmentPhone());

        Optional<User> userEntity = userRepository.findById(dto.getUserId());
        if (userEntity.isEmpty()) {
            throw new NoSuchElementException("Can't convert userId");
        }
        entity.setUser(userEntity.get());

        return entity;
    }

    // Evaluate
    public EvaluateDto toDTO(Evaluate entity) {
        EvaluateDto dto = new EvaluateDto();
        dto.setId(entity.getId());
        dto.setRate(entity.getRate());
        dto.setNote(entity.getNote());

        // candidateId and EmployerId
        return dto;
    }

    public Evaluate toEntity(EvaluateDto dto) {
        Evaluate entity = new Evaluate();
        entity.setId(dto.getId());
        entity.setRate(dto.getRate());
        entity.setNote(dto.getNote());

        // continue
        return entity;
    }

    // Experience
    public ExperienceDto toDTO(Experience entity) {
        ExperienceDto dto = new ExperienceDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setCompany(entity.getCompany());
        dto.setIsWorking(entity.getIsWorking());
        dto.setIndustry(entity.getIndustry());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setDescription(entity.getDescription());
        dto.setSkills(entity.getSkills());

        if (entity.getCandidate() == null) {
            throw new CustomException("Can't convert candidateEntity because it is null");
        }
        dto.setCandidateId(entity.getCandidate().getId());

        return dto;
    }

    public Experience toEntity(ExperienceDto dto) {
        Experience entity = new Experience();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setCompany(dto.getCompany());
        entity.setIsWorking(dto.getIsWorking());
        entity.setIndustry(dto.getIndustry());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDescription(dto.getDescription());
        entity.setSkills(dto.getSkills());

        Optional<Candidate> candidateEntity = candidateRepository.findById(dto.getCandidateId());
        if (candidateEntity.isPresent()) {
            // throw new NoSuchElementException("Can't convert candidateId");
            entity.setCandidate(candidateEntity.get());
        }

        return entity;
    }

    // Image
    public StorageFileDto toDTO(File entity) {
        StorageFileDto dto = new StorageFileDto();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setName(entity.getName());
        dto.setUrl(entity.getUrl());
        dto.setObjectId(entity.getObjectId());
        //dto.setAuthor(entity.getCreated_by());
        dto.setCloudinaryPublicId(entity.getCloudinaryPublicId());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public File toEntity(StorageFileDto dto) {
        File entity = new File();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setName(dto.getName());
        entity.setUrl(dto.getUrl());
        entity.setObjectId(dto.getObjectId());
        //entity.setCreated_by(dto.getAuthor());
        entity.setCloudinaryPublicId(dto.getCloudinaryPublicId());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }

    // JobAlert
    public JobAlertDto toDTO(JobAlert entity) {
        JobAlertDto dto = new JobAlertDto();
        dto.setId(entity.getId());
        dto.setTag(entity.getTag());
        dto.setIndustry(entity.getIndustry());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setWorkplaceType(entity.getWorkplaceType());
        dto.setCity(entity.getCity());
        dto.setMinBudget(entity.getMinBudget());

        if (entity.getCandidate() == null) {
            throw new CustomException("Can't convert candidateEntity because it is null");
        }
        dto.setCandidateId(entity.getCandidate().getId());

        return dto;
    }

    public JobAlert toEntity(JobAlertDto dto) {
        JobAlert entity = new JobAlert();
        entity.setId(dto.getId());
        entity.setTag(dto.getTag());
        entity.setIndustry(dto.getIndustry());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setWorkplaceType(dto.getWorkplaceType());
        entity.setCity(dto.getCity());
        entity.setMinBudget(dto.getMinBudget());

        Optional<Candidate> candidateEntity = candidateRepository.findById(dto.getCandidateId());
        if (candidateEntity.isPresent()) {
            // throw new NoSuchElementException("Can't convert candidateId");
            entity.setCandidate(candidateEntity.get());
        }

        return entity;
    }

    // JobPost
    public JobPostDtoOld toDTO(JobPost entity) {
        JobPostDtoOld dto = new JobPostDtoOld();
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
//        dto.setCreateTime(entity.getCreateTime());
        dto.setDueTime(entity.getDeadline());
        dto.setWorkStatus(entity.getWorkStatus());
        dto.setBlind(entity.isBlind());
        dto.setDeaf(entity.isDeaf());
        dto.setCommunicationDis(entity.isCommunicationDis());
        dto.setHandDis(entity.isHandDis());
        dto.setLabor(entity.isLabor());
        dto.setSkills(entity.getSkills());
//        dto.setPositions(entity.getPositions());
        dto.setViews(entity.getViews());
        dto.setActive(entity.getIsActive());
        dto.setDeleted(entity.getIsDeleted());

        if (entity.getCompany() != null) {
            dto.setLogo(entity.getCompany().getImage());
            dto.setCompanyId(entity.getCompany().getId());
        }

        if (entity.getCreatedEmployer() == null) {
            throw new CustomException("Can't convert createEntity because it is null");
        }
        dto.setCreatedEmployerId(entity.getCreatedEmployer().getId());

        return dto;
    }

    public JobPost toEntity(JobPostDtoOld dto) {
        JobPost entity = new JobPost();
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
//        entity.setCreateTime(dto.getCreateTime());
        entity.setDeadline(dto.getDueTime());
        entity.setWorkStatus(dto.getWorkStatus());
        entity.setBlind(dto.isBlind());
        entity.setDeaf(dto.isDeaf());
        entity.setCommunicationDis(dto.isCommunicationDis());
        entity.setHandDis(dto.isHandDis());
        entity.setLabor(dto.isLabor());
        entity.setSkills(dto.getSkills());
        entity.setViews(dto.getViews());
//        entity.setPositions(dto.getPositions());
        entity.setIsActive(dto.isActive());

        Optional<Employer> createEmployer =
                employerRepository.findById(dto.getCreatedEmployerId());
        if (createEmployer.isEmpty()) {
            throw new NoSuchElementException("Can't convert createEmployerId");
        }
        entity.setCreatedEmployer(createEmployer.get());
        System.out.println("category Id = " + dto.getCategoryId());
        Optional<Category> categoryEntity =
                categoryRepository.findById(dto.getCategoryId());

        if (categoryEntity.isEmpty()) {
            throw new NoSuchElementException("Can't convert categoryId");
        }
        System.out.println("category  = " + categoryEntity.get().getName());
        entity.setCategory(categoryEntity.get());

        if (dto.getCompanyId() != -1) {
            Optional<Company> company =
                    companyRepository.findById(dto.getCompanyId());
            if (company.isPresent()) {
                entity.setCompany(company.get());
            }
        }

        return entity;
    }

    // Notification
    public NotificationDto toDTO(Notification entity) {
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setTitle(entity.getTitle());
        dto.setBrief(entity.getBrief());
        dto.setTime(entity.getTime());

        return dto;
    }

    public Notification toEntity(NotificationDto dto) {
        Notification entity = new Notification();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setTitle(dto.getTitle());
        entity.setBrief(dto.getBrief());
        entity.setTime(dto.getTime());

        return entity;
    }

    public ExtraInfoDto toDTO(ExtraInfo entity) {
        ExtraInfoDto dto = new ExtraInfoDto();
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setIsActive(entity.getIsActive());

        return dto;
    }

    public ExtraInfo toEntity(ExtraInfoDto dto) {
        ExtraInfo entity = new ExtraInfo();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setIsActive(dto.getIsActive());

        return entity;
    }

    public InvoiceDto toDTO(Invoice entity) {
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

    public Invoice toEntity(InvoiceDto dto) {
        Invoice entity = new Invoice();
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

    public PackageDto toDTO(Package entity) {
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

    public Package toEntity(PackageDto dto) {
        Package entity = new Package();
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

    public SubscribeDto toDTO(Subscription entity) {
        SubscribeDto dto = new SubscribeDto();
        dto.setId(entity.getId());
        dto.setStartTime(entity.getStartTime());
        dto.setExpirationTime(entity.getExpirationTime());
        dto.setTransactionCode(entity.getPaymentTransactionCode());
        return dto;
    }

    public Subscription toEntity(SubscribeDto dto) {
        Subscription entity = new Subscription();
        entity.setId(dto.getId());
        entity.setStartTime(dto.getStartTime());
        entity.setExpirationTime(dto.getExpirationTime());
        entity.setPaymentTransactionCode(dto.getTransactionCode());
        return entity;
    }

    public CategoryDto toDTO(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public Category toEntity(CategoryDto dto) {
        Category entity = new Category();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }

    public BlogPostDto toDTO(BlogPost entity) {
//    // setup
//    TypeMap<BlogPostEntity, BlogPostDto> propertyMapper = modelMapper.createTypeMap(BlogPostEntity.class, BlogPostDto.class);
//    // add deep mapping to flatten source's Player object into a single field in destination
//    propertyMapper.addMappings(
//            mapper -> mapper.map(src -> src.getUserEntity().getId(), BlogPostDto::setUserId)
//    );
        BlogPostDto blogPostDTO = modelMapper.map(entity, BlogPostDto.class);
        return blogPostDTO;
    }

    public BlogPost toEntity(BlogPostDto dto) {
        BlogPost entity = modelMapper.map(dto, BlogPost.class);

        try {
            Optional<User> userEntity = userRepository.findById(dto.getUserId());
            if (userEntity.isPresent()) {
                entity.setAuthor(userEntity.get());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return entity;
    }

    public CommentDto toDTO(Comment entity) {
        CommentDto dto = modelMapper.map(entity, CommentDto.class);
        return dto;
    }

    public Comment toEntity(CommentDto dto) {
        Comment entity = modelMapper.map(dto, Comment.class);
        try {
            Optional<BlogPost> blogPostEntity = blogPostRepository.findById(dto.getBlogPostId());
            if (blogPostEntity.isPresent()) {
                entity.setBlogPost(blogPostEntity.get());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return entity;
    }

    public SubCommentDto toDTO(SubComment entity) {
        SubCommentDto dto = modelMapper.map(entity, SubCommentDto.class);
        return dto;
    }

    public SubComment toEntity(SubCommentDto dto) {
        SubComment entity = modelMapper.map(dto, SubComment.class);
        try {
            Optional<Comment> commentEntity = commentRepository.findById(dto.getCommentId());
            if (commentEntity.isPresent()) {
                entity.setComment(commentEntity.get());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return entity;
    }
}
