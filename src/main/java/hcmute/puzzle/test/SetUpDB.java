package hcmute.puzzle.test;

import static hcmute.puzzle.utils.Constant.SYSTEM_MAIL;

import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.FileType;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.utils.Constant;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SetUpDB {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JobPostRepository jobPostRepository;

    @Autowired
    ExperienceRepository experienceRepository;

    @Autowired
    ExtraInfoRepository extraInfoRepository;

    @Autowired
    CandidateRepository candidateRepository;

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    FileTypeRepository fileTypeRepository;

    public void preStart() {

        List<RoleEntity> roles = new ArrayList<>();
        long num = roleRepository.count();

        if (num == 0) {
            List<String> roleCodes = new ArrayList<>();

            roleCodes.add("user");
            roleCodes.add("admin");
            roleCodes.add("candidate");
            roleCodes.add("employer");

            roleCodes.stream()
                    .forEach(
                            code -> {
                                RoleEntity role = new RoleEntity();
                                role.setCode(code);
                                role.setName(code.toUpperCase());
                                roles.add(role);
                            });

            roleRepository.saveAll(roles);
        }

        RoleEntity userRole1 = new RoleEntity();
        userRole1.setCode("admin");
        userRole1.setName(userRole1.getCode().toUpperCase());

        RoleEntity userRole2 = new RoleEntity();
        userRole2.setCode("user");
        userRole2.setName(userRole2.getCode().toUpperCase());

        RoleEntity userRole3 = new RoleEntity();
        userRole3.setCode("employer");
        userRole3.setName(userRole3.getCode().toUpperCase());

        RoleEntity userRole4 = new RoleEntity();
        userRole4.setCode("candidate");
        userRole4.setName(userRole4.getCode().toUpperCase());

        Set<UserEntity> userList = new HashSet<>();

        // User
        UserEntity user1 = UserEntity.builder().email("candidate1@gmail.com").password(passwordEncoder.encode("123456"))
                .build();
        user1.getRoles().add(userRole4);
        user1.getRoles().add(userRole2);

        UserEntity user2 =
                UserEntity.builder()
                        .email("candidate2@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .build();
        user2.setRoles(new ArrayList<>());
        user2.getRoles().add(userRole4);
        user2.getRoles().add(userRole2);

        UserEntity user3 =
                UserEntity.builder()
                        .email("employer1@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .build();

        user3.setRoles(new ArrayList<>());
        user3.getRoles().add(userRole3);
        user3.getRoles().add(userRole2);

        UserEntity user4 = UserEntity.builder()
                .email("employer2@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .build();
        user4.getRoles().add(userRole3);
        user4.getRoles().add(userRole2);

        UserEntity user5 = UserEntity.builder()
                .email("admin1@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .build();
        user5.getRoles().add(userRole1);
        user5.getRoles().add(userRole2);

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);

        userRepository.saveAll(userList);

        // Candidate
        Set<CandidateEntity> candidateList = new HashSet<>();
        CandidateEntity candidate1 =
                CandidateEntity.builder()
                        .firstName("Minh")
                        .lastName("Lê Quang")
                        .skills("flutter#golang")
                        .emailContact("haovu961@gmail.com")
                        .build();
        candidate1.setUserEntity(user1);
        // user1.setCandidateEntity(candidate1);

        CandidateEntity candidate2 =
                CandidateEntity.builder()
                        .firstName("Phong")
                        .lastName("Vũ")
                        .skills("java#android#c#python")
                        .emailContact("haovu961@gmail.com")
                        .build();
        candidate2.setUserEntity(user2);
        // user2.setCandidateEntity(candidate2);
        candidateList.add(candidate1);
        candidateList.add(candidate2);

        // Employer
        Set<EmployerEntity> employerList = new HashSet<>();
        EmployerEntity employer1 = new EmployerEntity();
        employer1.setFirstname("Văn");
        employer1.setLastname("Minh");
        employer1.setUserEntity(user3);
        // user3.setEmployerEntity(employer1);
        employerList.add(employer1);

        EmployerEntity employer2 = new EmployerEntity();
        employer2.setFirstname("Văn");
        employer2.setLastname("Hoàng");
        employer2.setUserEntity(user4);
        // user4.setEmployerEntity(employer2);
        // userRepository.saveAll(userList);
        employerList.add(employer2);

        candidateRepository.saveAll(candidateList);
        employerRepository.saveAll(employerList);

        // Company
        Set<CompanyEntity> companyList = new HashSet<>();

        CompanyEntity company1 = new CompanyEntity();
        company1.setName("FPT soft");
        company1.setCreatedEmployer(employer1);

        CompanyEntity company2 = new CompanyEntity();
        company2.setName("Shopee");
        company2.setCreatedEmployer(employer2);

        CompanyEntity company3 = new CompanyEntity();
        company3.setName("Zalo");
        company3.setCreatedEmployer(employer1);

        companyList.add(company1);
        companyList.add(company2);
        companyList.add(company3);

        companyRepository.saveAll(companyList);

        // Job post
        Set<JobPostEntity> jobPostList = new HashSet<>();
        JobPostEntity jobPost1 = new JobPostEntity();
        jobPost1.setTitle("Nhân viên Sale");
        jobPost1.setEmploymentType("PART_TIME");
        jobPost1.setWorkplaceType("OFFICE");
        jobPost1.setDescription("Nhân viên bán hàng lương cứng 8 M, hoa hồng theo doanh thu");
        jobPost1.setCity("Tp Hồ Chí Minh");
        jobPost1.setAddress("Linh Tây, Thủ Đức, Tp.Hồ Chí Minh");
        jobPost1.setEducationLevel("Cao Đẳng");
        jobPost1.setExperienceYear(1);
        jobPost1.setQuantity(3);
        jobPost1.setMinBudget(800);
        jobPost1.setMaxBudget(1000);
        jobPost1.setCreatedEmployer(employer1);

        JobPostEntity jobPost2 = new JobPostEntity();
        jobPost2.setTitle("Dev java");
        jobPost2.setEmploymentType("FULL_TIME");
        jobPost2.setWorkplaceType("OFFICE");
        jobPost2.setDescription("Nhân viên bán hàng lương cứng 8 M, hoa hồng theo doanh thu");
        jobPost2.setCity("Tp Hồ Chí Minh");
        jobPost2.setAddress("Linh Tây, Thủ Đức, Tp.Hồ Chí Minh");
        jobPost2.setEducationLevel("Cao Đẳng");
        jobPost2.setExperienceYear(1);
        jobPost2.setQuantity(3);
        jobPost2.setMinBudget(800);
        jobPost2.setMaxBudget(1000);
        jobPost2.setCreatedEmployer(employer2);

        JobPostEntity jobPost3 = new JobPostEntity();
        jobPost3.setTitle("Nhân viên Marketing");
        jobPost3.setEmploymentType("PART_TIME");
        jobPost3.setWorkplaceType("OFFICE");
        jobPost3.setDescription("Nhân viên bán hàng lương cứng 8 M, hoa hồng theo doanh thu");
        jobPost3.setCity("Tp Hồ Chí Minh");
        jobPost3.setAddress("Linh Tây, Thủ Đức, Tp.Hồ Chí Minh");
        jobPost3.setEducationLevel("Cao Đẳng");
        jobPost3.setExperienceYear(1);
        jobPost3.setQuantity(3);
        jobPost3.setMinBudget(800);
        jobPost3.setMaxBudget(1000);

        jobPost3.setCreatedEmployer(employer2);

        jobPostList.add(jobPost1);
        jobPostList.add(jobPost2);
        jobPostList.add(jobPost3);

        jobPostRepository.saveAll(jobPostList);

        Set<ExtraInfoEntity> extraInfos = new HashSet<>();

        ExtraInfoEntity service1 = new ExtraInfoEntity();
        service1.setName("Java Develop");
        service1.setType("SERVICE");
        service1.setActive(true);

        ExtraInfoEntity service2 = new ExtraInfoEntity();
        service2.setName("Mobile Develop");
        service1.setType("SERVICE");
        service2.setActive(true);

        ExtraInfoEntity service3 = new ExtraInfoEntity();
        service3.setName("dot Net Develop");
        service1.setType("SERVICE");
        service3.setActive(false);

        ExtraInfoEntity service4 = new ExtraInfoEntity();
        service4.setName("Flutter Develop");
        service1.setType("SERVICE");
        service4.setActive(true);

        extraInfos.add(service1);
        extraInfos.add(service2);
        extraInfos.add(service3);
        extraInfos.add(service4);

        ExtraInfoEntity skill1 = new ExtraInfoEntity();
        skill1.setName("Java");
        skill1.setType("SKILL");
        skill1.setActive(true);

        ExtraInfoEntity skill2 = new ExtraInfoEntity();
        skill2.setName("Mobile Develop");
        skill1.setType("SKILL");
        skill2.setActive(true);

        ExtraInfoEntity skill3 = new ExtraInfoEntity();
        skill2.setName("dot Net Develop");
        skill1.setType("SKILL");
        skill2.setActive(false);

        ExtraInfoEntity skill4 = new ExtraInfoEntity();
        skill2.setName("Flutter Develop");
        skill1.setType("SKILL");
        skill2.setActive(true);

        extraInfos.add(skill1);
        extraInfos.add(skill2);
        extraInfos.add(skill3);
        extraInfos.add(skill4);

        ExtraInfoEntity position1 = new ExtraInfoEntity();
        position1.setName("Java Develop");
        position1.setType("POSITION");
        position1.setActive(true);

        ExtraInfoEntity position2 = new ExtraInfoEntity();
        position2.setName("Mobile Develop");
        position2.setType("POSITION");
        position2.setActive(true);

        ExtraInfoEntity position3 = new ExtraInfoEntity();
        position3.setName("Dot Net Develop");
        position3.setType("POSITION");
        position3.setActive(false);

        ExtraInfoEntity position4 = new ExtraInfoEntity();
        position4.setName("Java Develop");
        position4.setType("POSITION");
        position4.setActive(true);

        extraInfos.add(position1);
        extraInfos.add(position2);
        extraInfos.add(position3);
        extraInfos.add(position4);

        extraInfoRepository.saveAll(extraInfos);

        //    Set<ExtraInfoEntity> extraInfoList = new HashSet<>();
        //    ExtraInfoEntity extraInfo1 = new ExtraInfoEntity();
        //    extraInfo1.setActive(true);
        //    extraInfo1.setName("Java Develop");
        //    extraInfo1.setType("skill");
        //
        //    extraInfoList.add(extraInfo1);
        //
        //    ExtraInfoEntity extraInfo2 = new ExtraInfoEntity();
        //    extraInfo1.setActive(true);
        //    extraInfo1.setName("Java Develop");
        //    extraInfo1.setType("position");
        //
        //    extraInfoList.add(extraInfo2);
        //
        //    extraInfoRepository.saveAll(extraInfoList);
    }

    public void createMdFileType() {
        FileTypeEntity avatarType =
                FileTypeEntity.builder()
                        .category(FileCategory.IMAGE_AVATAR)
                        .type(FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_IMAGE_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        FileTypeEntity companyType =
                FileTypeEntity.builder()
                        .category(FileCategory.IMAGE_COMPANY)
                        .type(FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_COMPANY_IMAGE_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        FileTypeEntity blogImageType =
                FileTypeEntity.builder()
                        .category(FileCategory.IMAGE_BLOG)
                        .type(FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_BLOG_IMAGE_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        FileTypeEntity blogThumbnailType =
                FileTypeEntity.builder()
                        .category(FileCategory.THUMBNAIL_BLOGPOST)
                        .type(FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_BLOG_THUMBNAIL_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        List<FileTypeEntity> fileTypeList =
                new ArrayList<>(Arrays.asList(avatarType, companyType, blogImageType, blogThumbnailType));
        fileTypeRepository.saveAll(fileTypeList);
    }
}
