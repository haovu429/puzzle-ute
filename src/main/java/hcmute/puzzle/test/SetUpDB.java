package hcmute.puzzle.test;

import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.models.enums.ExtraInfoType;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static hcmute.puzzle.utils.Constant.SYSTEM_MAIL;

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

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BlogCategoryRepository blogCategoryRepository;

    @Autowired
    BlogPostRepository blogPostRepository;

    public void preStart() {

        List<Role> roles = new ArrayList<>();
        Long num = roleRepository.count();

        //        if (num == 0) {
        //            List<String> roleCodes = new ArrayList<>();
        //
        //            roleCodes.add("user");
        //            roleCodes.add("admin");
        //            roleCodes.add("candidate");
        //            roleCodes.add("employer");
        //
        //            roleCodes.stream()
        //                    .forEach(
        //                            code -> {
        //                                RoleEntity role = new RoleEntity();
        //                                role.setCode(code);
        //                                role.setName(code.toUpperCase());
        //                                roles.add(role);
        //                            });
        //
        //            roleRepository.saveAll(roles);
        //        }

        Role userRole1 = new Role();
        userRole1.setCode("admin");
        userRole1.setName(userRole1.getCode().toUpperCase());

        Role userRole2 = new Role();
        userRole2.setCode("user");
        userRole2.setName(userRole2.getCode().toUpperCase());

        Role userRole3 = new Role();
        userRole3.setCode("employer");
        userRole3.setName(userRole3.getCode().toUpperCase());

        Role userRole4 = new Role();
        userRole4.setCode("candidate");
        userRole4.setName(userRole4.getCode().toUpperCase());

        roles.add(userRole1);
        roles.add(userRole2);
        roles.add(userRole3);
        roles.add(userRole4);

        roleRepository.saveAll(roles);

        Set<User> userList = new HashSet<>();

        // User
        User user1 = User.builder()
                         .email("candidate1@gmail.com")
                         .password(passwordEncoder.encode("123456"))
                         .isActive(true)
                         .emailVerified(true)
                         .build();
        user1.getRoles().add(userRole4);
        user1.getRoles().add(userRole2);

        User user2 = User.builder()
                         .email("candidate2@gmail.com")
                         .password(passwordEncoder.encode("123456"))
                         .isActive(true)
                         .emailVerified(true)
                         .build();
        //        user2.setRoles(new HashSet<>());
        user2.getRoles().add(userRole4);
        user2.getRoles().add(userRole2);

        User user3 = User.builder()
                         .email("employer1@gmail.com")
                         .password(passwordEncoder.encode("123456"))
                         .isActive(true)
                         .emailVerified(true)
                         .build();

        //        user3.setRoles(new HashSet<>());
        user3.getRoles().add(userRole3);
        user3.getRoles().add(userRole2);

        User user4 = User.builder()
                         .email("employer2@gmail.com")
                         .password(passwordEncoder.encode("123456"))
                         .isActive(true)
                         .emailVerified(true)
                         .build();
        user4.getRoles().add(userRole3);
        user4.getRoles().add(userRole2);

        User user5 = User.builder()
                         .email("admin1@gmail.com")
                         .password(passwordEncoder.encode("123456"))
                         .isActive(true)
                         .emailVerified(true)
                         .isAdmin(true)
                         .build();
        user5.getRoles().add(userRole1);
        user5.getRoles().add(userRole2);


        User user6 = User.builder()
                         .email("admin2@gmail.com")
                         .password(passwordEncoder.encode("123456"))
                         .isActive(true)
                         .emailVerified(true)
                         .isAdmin(true)
                         .build();
        user6.getRoles().add(userRole1);
        user6.getRoles().add(userRole2);

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userList.add(user6);

        userRepository.saveAll(userList);

        // Candidate
        Set<Candidate> candidateList = new HashSet<>();
        Candidate candidate1 = Candidate.builder()
										.firstName("Minh")
										.lastName("Lê Quang")
										.skills("flutter#golang")
										.emailContact("haovu961@gmail.com")
										.build();
        candidate1.setUser(user1);
        // user1.setCandidateEntity(candidate1);

        Candidate candidate2 = Candidate.builder()
										.firstName("Phong")
										.lastName("Vũ")
										.skills("java#android#c#python")
										.emailContact("haovu961@gmail.com")
										.build();
        candidate2.setUser(user2);
        // user2.setCandidateEntity(candidate2);
        candidateList.add(candidate1);
        candidateList.add(candidate2);

        // Employer
        Set<Employer> employerList = new HashSet<>();
        Employer employer1 = new Employer();
        employer1.setFirstName("Văn");
        employer1.setLastName("Minh");
        employer1.setUser(user3);
        // user3.setEmployerEntity(employer1);
        employerList.add(employer1);

        Employer employer2 = new Employer();
        employer2.setFirstName("Văn");
        employer2.setLastName("Hoàng");
        employer2.setUser(user4);
        // user4.setEmployerEntity(employer2);
        // userRepository.saveAll(userList);
        employerList.add(employer2);

        candidateRepository.saveAll(candidateList);
        employerRepository.saveAll(employerList);

        // Company
        Set<Company> companyList = new HashSet<>();

        Company company1 = new Company();
        company1.setName("FPT soft");
        company1.setCreatedEmployer(employer1);

        Company company2 = new Company();
        company2.setName("Shopee");
        company2.setCreatedEmployer(employer2);

        Company company3 = new Company();
        company3.setName("Zalo");
        company3.setCreatedEmployer(employer1);

        companyList.add(company1);
        companyList.add(company2);
        companyList.add(company3);
        companyRepository.saveAll(companyList);

        Category category1 = Category.builder().name("IT").isActive(true).build();
        Category category2 = Category.builder().name("Marketing").isActive(true).build();
        Category category3 = Category.builder().name("Education").isActive(true).build();
        List<Category> categories = new ArrayList<>(List.of(category1, category2, category3));
        categoryRepository.saveAll(categories);

        BlogCategory blogCategory1 = BlogCategory.builder().name("It story").isActive(true).build();
        BlogCategory blogCategory2 = BlogCategory.builder().name("Interview skill").isActive(true).build();
        BlogCategory blogCategory3 = BlogCategory.builder().name("AI").isActive(true).build();
        List<BlogCategory> blogCategories = new ArrayList<>(List.of(blogCategory1, blogCategory2, blogCategory3));
        blogCategoryRepository.saveAll(blogCategories);

        BlogPost blogPost1 = BlogPost.builder()
                                     .title("Learn dsl in spring boot")
                                     .body("This is content about dsl, jpa spring boot")
                                     .tags("#jpa, #dsl")
                                     .thumbnail(
                                             "https://res.cloudinary.com/drwwfkcmg/image/upload/v1686509234/puzzle_ute/user/blog/z2716433391602_947c1acfa0646b29cb08c4e0a02b3d33.jpg2023-06-12T01:47:09_blog_image.jpg")
                                     .blogCategory(blogCategory1)
                                     .author(user2)
                                     .build();

        BlogPost blogPost2 = BlogPost.builder()
                                     .title("Test test is beautiful girl")
                                     .body("This post view a beautiful girl who is tester")
                                     .tags("#tester, #girl")
                                     .thumbnail(
                                             "https://res.cloudinary.com/drwwfkcmg/image/upload/v1686509234/puzzle_ute/user/blog/z2716433391602_947c1acfa0646b29cb08c4e0a02b3d33.jpg2023-06-12T01:47:09_blog_image.jpg")
                                     .blogCategory(blogCategory2)
                                     .author(user1)
                                     .build();

        BlogPost blogPost3 = BlogPost.builder()
                                     .title("How to master backend java")
                                     .body("This post list skills needed for a backend java dev")
                                     .tags("#java, #backend, #master, #dev")
                                     .thumbnail(
                                             "https://plopdo.com/wp-content/uploads/2021/10/What-is-back-end-development-2.jpg")
                                     .blogCategory(blogCategory1)
                                     .author(user3)
                                     .build();
        List<BlogPost> blogPosts = new ArrayList<>(List.of(blogPost1, blogPost2, blogPost3));
        blogPostRepository.saveAll(blogPosts);
        // Job post
        Set<JobPost> jobPostList = new HashSet<>();
        JobPost jobPost1 = new JobPost();
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
        jobPost1.setIsActive(true);
        jobPost1.setCreatedEmployer(employer1);
        jobPost1.setCategory(category1);

        JobPost jobPost2 = new JobPost();
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
        jobPost2.setIsActive(true);
        jobPost2.setCreatedEmployer(employer2);
        jobPost2.setCategory(category2);

        JobPost jobPost3 = JobPost.builder()
                                  .title("Nhân viên Marketing")
                                  .employmentType("PART_TIME")
                                  .workplaceType("OFFICE")
                                  .description("Nhân viên bán hàng lương cứng 8 M, hoa hồng theo doanh thu")
                                  .city("Tp Hồ Chí Minh")
                                  .address("Linh Tây, Thủ Đức, Tp.Hồ Chí Minh")
                                  .educationLevel("Cao Đẳng")
                                  .experienceYear(1)
                                  .quantity(3)
                                  .minBudget(800)
                                  .maxBudget(1000)
                                  .createdEmployer(employer2)
                                  .isActive(true)
                                  .category(category1)
                                  .build();

        //        jobPost3.setTitle("Nhân viên Marketing");
        //        jobPost3.setEmploymentType("PART_TIME");
        //        jobPost3.setWorkplaceType("OFFICE");
        //        jobPost3.setDescription("Nhân viên bán hàng lương cứng 8 M, hoa hồng theo doanh thu");
        //        jobPost3.setCity("Tp Hồ Chí Minh");
        //        jobPost3.setAddress("Linh Tây, Thủ Đức, Tp.Hồ Chí Minh");
        //        jobPost3.setEducationLevel("Cao Đẳng");
        //        jobPost3.setExperienceYear(1);
        //        jobPost3.setQuantity(3);
        //        jobPost3.setMinBudget(800);
        //        jobPost3.setMaxBudget(1000);
        //        jobPost3.setCreatedEmployer(employer2);

        jobPostList.add(jobPost1);
        jobPostList.add(jobPost2);
        jobPostList.add(jobPost3);

        jobPostRepository.saveAll(jobPostList);

        Set<ExtraInfo> extraInfos = new HashSet<>();

        ExtraInfo service1 = new ExtraInfo();
        service1.setName("Java Develop");
        service1.setType(ExtraInfoType.SERVICE);
        service1.setIsActive(true);

        ExtraInfo service2 = new ExtraInfo();
        service2.setName("Mobile Develop");
        service2.setType(ExtraInfoType.SERVICE);
        service2.setIsActive(true);

        ExtraInfo service3 = new ExtraInfo();
        service3.setName("dot Net Develop");
        service3.setType(ExtraInfoType.SERVICE);
        service3.setIsActive(true);

        ExtraInfo service4 = new ExtraInfo();
        service4.setName("Flutter Develop");
        service4.setType(ExtraInfoType.SERVICE);
        service4.setIsActive(true);

        extraInfos.add(service1);
        extraInfos.add(service2);
        extraInfos.add(service3);
        extraInfos.add(service4);

        ExtraInfo skill1 = new ExtraInfo();
        skill1.setName("Java");
        skill1.setType(ExtraInfoType.SKILL);
        skill1.setIsActive(true);

        ExtraInfo skill2 = new ExtraInfo();
        skill2.setName("Mobile Develop");
        skill2.setType(ExtraInfoType.SKILL);
        skill2.setIsActive(true);

        ExtraInfo skill3 = new ExtraInfo();
        skill3.setName("dot Net Develop");
        skill3.setType(ExtraInfoType.SKILL);
        skill3.setIsActive(true);

        ExtraInfo skill4 = new ExtraInfo();
        skill4.setName("Flutter Develop");
        skill4.setType(ExtraInfoType.SKILL);
        skill4.setIsActive(true);

        extraInfos.add(skill1);
        extraInfos.add(skill2);
        extraInfos.add(skill3);
        extraInfos.add(skill4);

        ExtraInfo position1 = new ExtraInfo();
        position1.setName("Java Develop");
        position1.setType(ExtraInfoType.POSITION);
        position1.setIsActive(true);

        ExtraInfo position2 = new ExtraInfo();
        position2.setName("Mobile Develop");
        position2.setType(ExtraInfoType.POSITION);
        position2.setIsActive(true);

        ExtraInfo position3 = new ExtraInfo();
        position3.setName("Dot Net Develop");
        position3.setType(ExtraInfoType.POSITION);
        position3.setIsActive(true);

        ExtraInfo position4 = new ExtraInfo();
        position4.setName("Java Develop");
        position4.setType(ExtraInfoType.POSITION);
        position4.setIsActive(true);

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
        FileType avatarType =
                FileType.builder()
                        .category(FileCategory.IMAGE_AVATAR)
                        .type(hcmute.puzzle.infrastructure.models.enums.FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_IMAGE_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        FileType categoryType =
                FileType.builder()
                        .category(FileCategory.IMAGE_CATEGORY)
                        .type(hcmute.puzzle.infrastructure.models.enums.FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_CATEGORY_IMAGE_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        FileType companyType =
                FileType.builder()
                        .category(FileCategory.IMAGE_COMPANY)
                        .type(hcmute.puzzle.infrastructure.models.enums.FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_COMPANY_IMAGE_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        FileType blogImageType =
                FileType.builder()
                        .category(FileCategory.IMAGE_BLOG)
                        .type(hcmute.puzzle.infrastructure.models.enums.FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_BLOG_IMAGE_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();

        FileType blogThumbnailType =
                FileType.builder()
                        .category(FileCategory.THUMBNAIL_BLOGPOST)
                        .type(hcmute.puzzle.infrastructure.models.enums.FileType.IMAGE)
                        .location(Constant.FileLocation.STORAGE_BLOG_THUMBNAIL_LOCATION)
                        .storageName(Constant.StorageName.CLOUDINARY)
                        .author(SYSTEM_MAIL)
                        .build();


        List<FileType> fileTypeList =
                new ArrayList<>(Arrays.asList(avatarType, companyType, blogImageType, blogThumbnailType));
        fileTypeRepository.saveAll(fileTypeList);
    }
}
