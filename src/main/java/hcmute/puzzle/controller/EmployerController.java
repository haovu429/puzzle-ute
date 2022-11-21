package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.ApplicationEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.ApplicationService;
import hcmute.puzzle.services.CompanyService;
import hcmute.puzzle.services.EmployerService;
import hcmute.puzzle.services.JobPostService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class EmployerController {
  @Autowired EmployerService employerService;

  @Autowired UserRepository userRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired JobPostService jobPostService;

  @Autowired Converter converter;

  @Autowired CompanyService companyService;

  @Autowired ApplicationRepository applicationRepository;

  @Autowired ApplicationService applicationService;

  @DeleteMapping("/employer/{id}")
  ResponseObject delete(
      @PathVariable Long id, @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    return employerService.delete(linkUser.get().getId());
  }

  @PutMapping("/employer/update")
  ResponseObject update(
      @RequestBody @Validated EmployerDTO employer,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization") String token) {
    if (bindingResult.hasErrors()) {
      throw new CustomException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    employer.setUserId(linkUser.get().getId());
    employer.setId(linkUser.get().getId());

    return employerService.update(employer);
  }

  @GetMapping("/employer/profile")
  ResponseObject getById(@RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }
    return employerService.getOne(linkUser.get().getId());
  }

  @PostMapping("/employer/post-job")
  ResponseObject postJob(
      @RequestBody @Validated JobPostDTO jobPostDTO,
      @RequestHeader(value = "Authorization") String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }

    // Validate JobPost
    jobPostService.validateJobPost(jobPostDTO);

    // Set default createEmployer is Employer create first (this valid user requesting)
    jobPostDTO.setCreatedEmployerId(linkUser.get().getId());

    return new ResponseObject(
        HttpStatus.OK.value(), "Post job success.", jobPostService.add(jobPostDTO));
  }

  @DeleteMapping("/employer/delete-job-post/{id}")
  ResponseObject deleteJobPost(
      @PathVariable Long id, @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // Check rights
    pipelineCheckRights(id, linkUser);
    return jobPostService.delete(id);
  }

  @PutMapping("/employer/update-job-post")
  ResponseObject updateJobPost(
      @RequestBody @Validated JobPostDTO jobPostDTO,
      @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    // Check rights
    pipelineCheckRights(jobPostDTO.getId(), linkUser);

    // Validate JobPost
    jobPostService.validateJobPost(jobPostDTO);

    // employer can not change active status
    Optional<JobPostEntity> oldJobPost = jobPostRepository.findById(jobPostDTO.getId());
    if (oldJobPost.isEmpty()) {
      throw new CustomException("Job post isn't exists");
    }
    jobPostDTO.setActive(oldJobPost.get().isActive());

    // Set default createEmployer is Employer create first (this valid user requesting)
    jobPostDTO.setCreatedEmployerId(linkUser.get().getId());

    return new ResponseObject(
        HttpStatus.OK.value(), "Post job success.", jobPostService.update(jobPostDTO));
  }

  void pipelineCheckRights(long jobPostId, Optional<UserEntity> linkUser) {

    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }

    Optional<JobPostEntity> jobPostEntity = jobPostRepository.findById(jobPostId);
    // Check job post exists
    if (jobPostEntity.isEmpty()) {
      throw new CustomException("Job post isn't exists!");
    }

    // Check employer update is created employer.
    if (jobPostEntity.get().getCreatedEmployer().getId() != linkUser.get().getId()) {
      throw new CustomException("You have no rights to this post!");
    }
  }

  @PostMapping("/create-info-company")
  public ResponseObject saveCompany(
      @RequestBody CompanyDTO companyDTO,
      @RequestHeader(value = "Authorization", required = true) String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }

    companyDTO.setActive(false);
    companyDTO.setCreatedEmployerId(linkUser.get().getId());
    return companyService.save(companyDTO);
  }

  // Lấy danh sách ứng viên đã apply vào một jobPost
  @GetMapping("/employer/candidate-apply-jobpost/{jobPostId}")
  ResponseObject getCandidateApplyJobPost(
      @PathVariable Long jobPostId, @RequestHeader(value = "Authorization") String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }

    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);

    if (jobPost.isEmpty()) {
      throw new CustomException("JobPost isn't exist");
    }

    if (jobPost.get().getCreatedEmployer().getId() != linkUser.get().getId()) {
      throw new CustomException("You don't have rights for this jobPost");
    }

    return jobPostService.getCandidatesApplyJobPost(jobPostId);
  }

  @GetMapping("/employer/response-application")
  ResponseObject responseApplication(
      @RequestParam long applicationId,
      @RequestParam boolean result,
      @RequestParam String note,
      @RequestHeader(value = "Authorization") String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }

    Optional<ApplicationEntity> application = applicationRepository.findById(applicationId);

    if (application.isEmpty()) {
      throw new CustomException("Application isn't exist");
    }

    if (application.get().getJobPostEntity().getCreatedEmployer().getId()
        != linkUser.get().getId()) {
      throw new CustomException("You don't have rights for this application");
    }

    return applicationService.responseApplication(applicationId, result, note);
  }

  @GetMapping("/employer/get-all-job-post-created")
  ResponseObject getJobPostCreated(@RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }

    return jobPostService.getJobPostCreatedByEmployerId(linkUser.get().getId());

  }

  @GetMapping("/employer/get-application-by-job-post/{jobPostId}")
  ResponseObject getApplicationByJobPost(@RequestHeader(value = "Authorization") String token, @PathVariable long jobPostId) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    // Check is Employer
    if (linkUser.get().getEmployerEntity() == null) {
      throw new CustomException("This account isn't Employer");
    }

    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    if (jobPost.isEmpty()) {
      throw new CustomException("Job post isn't exists");
    }

    if (jobPost.get().getCreatedEmployer().getId() != linkUser.get().getId()) {
      throw new CustomException("You don't have rights for this job post");
    }

    return applicationService.getApplicationByJobPostId(jobPostId);
  }
}
