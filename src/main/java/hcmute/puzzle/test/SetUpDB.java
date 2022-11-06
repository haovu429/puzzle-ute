package hcmute.puzzle.test;

import hcmute.puzzle.entities.*;
import hcmute.puzzle.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SetUpDB {
  @Autowired RoleRepository roleRepository;

  @Autowired CompanyRepository companyRepository;

  @Autowired UserRepository userRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired ServiceRepository serviceRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired EmployerRepository employerRepository;

  public void preStart() {

    List<RoleEntity> roles = new ArrayList<>();
    long num = roleRepository.count();

    if (num == 0) {
      List<String> roleCodes = new ArrayList<>();

      roleCodes.add("user");
      roleCodes.add("admin");

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
    UserEntity user1 = new UserEntity();
    user1.setEmail("candidate1@gmail.com");
    user1.setPassword(passwordEncoder.encode("123456"));
    user1.getRoles().add(userRole4);
    user1.getRoles().add(userRole2);

    UserEntity user2 = new UserEntity();
    user2.setEmail("candidate2@gmail.com");
    user2.setPassword(passwordEncoder.encode("123456"));
    user2.getRoles().add(userRole4);
    user2.getRoles().add(userRole2);

    UserEntity user3 = new UserEntity();
    user3.setEmail("employer1@gmail.com");
    user3.setPassword(passwordEncoder.encode("123456"));
    user3.getRoles().add(userRole3);
    user3.getRoles().add(userRole2);

    UserEntity user4 = new UserEntity();
    user4.setEmail("employer2@gmail.com");
    user4.setPassword(passwordEncoder.encode("123456"));
    user4.getRoles().add(userRole3);
    user4.getRoles().add(userRole2);

    UserEntity user5 = new UserEntity();
    user5.setEmail("admin1@gmail.com");
    user5.setPassword(passwordEncoder.encode("123456"));
    user5.getRoles().add(userRole1);

    userList.add(user1);
    userList.add(user2);
    userList.add(user3);
    userList.add(user4);
    userList.add(user5);

    userRepository.saveAll(userList);

    // Candidate
    Set<CandidateEntity> candidateList = new HashSet<>();
    CandidateEntity candidate1 = new CandidateEntity();
    candidate1.setFirstName("Minh");
    candidate1.setLastName("Lê Quang");
    candidate1.setUserEntity(user1);
    // user1.setCandidateEntity(candidate1);

    CandidateEntity candidate2 = new CandidateEntity();
    candidate2.setFirstName("Phong");
    candidate2.setLastName("Vũ");
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

    CompanyEntity company2 = new CompanyEntity();
    company2.setName("Shopee");

    CompanyEntity company3 = new CompanyEntity();
    company3.setName("Zalo");

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

    Set<ServiceEntity> serviceList = new HashSet<>();
    ServiceEntity service1 = new ServiceEntity();
    service1.setName("Java Develop");
    service1.setActive(true);

    ServiceEntity service2 = new ServiceEntity();
    service2.setName("Mobile Develop");
    service2.setActive(true);

    ServiceEntity service3 = new ServiceEntity();
    service3.setName("dot Net Develop");
    service3.setActive(false);

    ServiceEntity service4 = new ServiceEntity();
    service4.setName("Flutter Develop");
    service4.setActive(true);

    serviceList.add(service1);
    serviceList.add(service2);
    serviceList.add(service3);
    serviceList.add(service4);

    serviceRepository.saveAll(serviceList);
  }
}
