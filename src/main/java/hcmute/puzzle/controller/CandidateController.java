package hcmute.puzzle.controller;

import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.ExperienceDTO;
import hcmute.puzzle.dto.JobAlertDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.ApplicationEntity;
import hcmute.puzzle.entities.ExperienceEntity;
import hcmute.puzzle.entities.JobAlertEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.*;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path = "/candidate")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

  @Autowired ApplicationService applicationService;
  @Autowired CandidateService candidateService;

  @Autowired UserRepository userRepository;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired ApplicationRepository applicationRepository;

  @Autowired JobAlertRepository jobAlertRepository;

  @Autowired JobAlertService jobAlertService;

  @Autowired ExperienceService experienceService;

  @Autowired ExperienceRepository experienceRepository;

  @Autowired EmployerService employerService;

  @Autowired CompanyService companyService;

  @Autowired JobPostService jobPostService;

  @PostMapping("/add")
  ResponseObject saveCandidate(
      @RequestBody @Validated CandidateDTO candidate,
      BindingResult bindingResult,
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    if (userDetails.getUser().getCandidateEntity() != null) {
      throw new CustomException("Info candidate for this account was created!");
    }
    candidate.setUserId(userDetails.getUser().getId());

    Optional<CandidateDTO> candidateDTO = candidateService.save(candidate);
    if (candidateDTO.isPresent()) {
      return new ResponseObject(
          HttpStatus.OK.value(), "Create candidate successfully", candidateDTO.get());
    } else {
      throw new RuntimeException("Add candidate failed");
    }

    //        return new ResponseObject(
    //                HttpStatus.OK.value(), "Create candidate successfully", new CandidateDTO());
  }

  // Gửi Authentication xác thực tài khoản thì xoá.
  @DeleteMapping("/candidate")
  ResponseObject deleteCandidate(Authentication authentication) {

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return candidateService.delete(userDetails.getUser().getId());
  }

  @PutMapping("/update")
  ResponseObject updateCandidate(
      @RequestBody @Validated CandidateDTO candidate,
      BindingResult bindingResult,
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    candidate.setUserId(userDetails.getUser().getId());
    candidate.setId(userDetails.getUser().getId());
    return candidateService.update(candidate);
  }

  // public
  @GetMapping("/profile")
  ResponseObject getById(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return candidateService.getOne(userDetails.getUser().getId());
  }

  @GetMapping("/follow-employer/{id}")
  ResponseObject followEmployer(
      @PathVariable(value = "id") Long employerId,
      // @RequestBody Map<String, Object> input,
      // @RequestParam(name = "employerId") long employerId,
      Authentication authentication) {
    // Map<String, Object> retMap = new HashMap<String, Object>();

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    // long candidateId = linkUser.get().getId();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    // long candidateId = ((Number) input.get("candidateId")).longValue();
    // long employerId = ((Number) input.get("employerId")).longValue();

    return candidateService.followEmployer(userDetails.getUser().getId(), employerId);
  }

  @GetMapping("/cancel-followed-employer/{id}")
  ResponseObject cancelFollowEmployer(
      @PathVariable(value = "id") Long employerId, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return candidateService.cancelFollowedEmployer(userDetails.getUser().getId(), employerId);
  }

  @GetMapping("/follow-company/{id}")
  ResponseObject followCompany(
      @PathVariable(value = "id") Long companyId, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return candidateService.followCompany(userDetails.getUser().getId(), companyId);
  }

  @GetMapping("/cancel-followed-company/{id}")
  ResponseObject cancelFollowCompany(
      @PathVariable(value = "id") Long companyId, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return candidateService.cancelFollowedCompany(userDetails.getUser().getId(), companyId);
  }

  @GetMapping("/apply-job-post/{postId}")
  DataResponse applyJobPost(@PathVariable Long postId, Authentication authentication) {

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<JobPostEntity> jobPost = jobPostRepository.findById(postId);

    if (jobPost.isEmpty()) {
      throw new NoSuchElementException("JobPost no value present");
    }

    if (!jobPost.get().isActive()) {
      // throw new CustomException("You can't apply this jobPost. It isn't active");
      return new DataResponse(
          DataResponse.CODE_ERROR_INACTIVE,
          "You can't apply this jobPost. It isn't active",
          DataResponse.STATUS_CUSTOM_EXCEPTION);
    }

    if (jobPost.get().getDueTime().before(new Date())) {
      // throw new CustomException("You can't apply this jobPost. It isn't active");
      return new DataResponse(
              DataResponse.CODE_ERROR_INACTIVE,
              "You can't apply this jobPost. job post has expired",
              DataResponse.STATUS_CUSTOM_EXCEPTION);
    }

    Optional<ApplicationEntity> application =
        applicationRepository.findApplicationByCanIdAndJobPostId(
            userDetails.getUser().getId(), postId);
    if (application.isPresent()) {
      // throw new CustomException("You applied for this job");
      return new DataResponse(
          DataResponse.CODE_ERROR_NOT_AGAIN, "You applied for this job", DataResponse.STATUS_NOT_AGAIN);
    }

    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setCandidateEntity(userDetails.getUser().getCandidateEntity());
    applicationEntity.setJobPostEntity(jobPost.get());
    //applicationEntity.setCreateTime(new Date());
    applicationRepository.save(applicationEntity);

    jobPost.get().getViewedUsers().add(userDetails.getUser());
    jobPostRepository.save(jobPost.get());

    return new DataResponse("Apply success");
  }

  @GetMapping("/cancel-apply-job-post/{postId}")
  ResponseObject cancelApplyJobPost(
      @PathVariable Long postId,
      Authentication authentication /*@RequestHeader(value = "Authorization") String token*/) {

    //    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    //
    //    if (linkUser.isEmpty()) {
    //      throw new CustomException("Not found account");
    //    }
    //
    //    if (linkUser.get().getCandidateEntity() == null) {
    //      throw new CustomException("This account isn't Candidate");
    //    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<ApplicationEntity> application =
        applicationRepository.findApplicationByCanIdAndJobPostId(
            userDetails.getUser().getId(), postId);
    if (!application.isPresent()) {
      throw new CustomException("You have not applied this JobPost or JobPost doesn't exist");
    }
    applicationRepository.delete(application.get());

    return new ResponseObject(200, "Cancel apply success", null);
  }

  @GetMapping("/save-job-post/{jobPostId}")
  ResponseObject saveJobPost(
      @PathVariable(value = "jobPostId") Long jobPostId, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return candidateService.saveJobPost(userDetails.getUser().getId(), jobPostId);
  }

  @GetMapping("/cancel-saved-job-post/{jobPostId}")
  ResponseObject cancelSaveJobPost(
      @PathVariable(value = "jobPostId") Long jobPostId,
      /*@RequestHeader(value = "Authorization") String token*/ Authentication authentication) {

    //    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    //
    //    if (linkUser.isEmpty()) {
    //      throw new CustomException("Not found account");
    //    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return candidateService.cancelSavedJobPost(userDetails.getUser().getId(), jobPostId);
  }

  @PostMapping("/add-job-alert")
  ResponseObject addJobAlert(
      @RequestBody @Validated JobAlertDTO jobAlertDTO,
      BindingResult bindingResult,
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return jobAlertService.save(userDetails.getUser().getId(), jobAlertDTO);
  }

  @PutMapping("/update-job-alert")
  ResponseObject updateJobAlert(
      @RequestBody @Validated JobAlertDTO jobAlertDTO,
      BindingResult bindingResult,
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<JobAlertEntity> jobAlert = jobAlertRepository.findById(jobAlertDTO.getId());

    if (jobAlert.isEmpty()) {
      throw new CustomException("Job Alert isn't exists");
    }

    if (jobAlert.get().getCandidateEntity().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this JobAlert");
    }

    jobAlertDTO.setCandidateId(userDetails.getUser().getId());

    return jobAlertService.update(jobAlertDTO);
  }

  @GetMapping("/delete-job-alert/{jobAlertId}")
  ResponseObject deleteJobAlert(
      @PathVariable(value = "jobAlertId") long id, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<JobAlertEntity> jobAlert = jobAlertRepository.findById(id);

    if (jobAlert.isEmpty()) {
      throw new CustomException("Job Alert isn't exists");
    }

    if (jobAlert.get().getCandidateEntity().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this JobAlert");
    }

    return jobAlertService.delete(id);
  }

  @GetMapping("/get-job-alert")
  ResponseObject getAllJobAlertByCandidateId(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return jobAlertService.getAllJobAlertByCandidateId(userDetails.getUser().getId());
  }

  @GetMapping("/get-job-alert-by-id/{jobAlertId}")
  ResponseObject getAllJobAlertById(
      @PathVariable(value = "jobAlertId") long jobAlertId, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<JobAlertEntity> jobAlert = jobAlertRepository.findById(jobAlertId);

    if (jobAlert.isEmpty()) {
      throw new CustomException("Job Alert have this id isn't exist");
    }

    if (jobAlert.get().getCandidateEntity().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have right for this Job Alert");
    }

    return jobAlertService.getOneById(jobAlertId);
  }

  @PostMapping("/add-experience")
  ResponseObject addExperience(
      @RequestBody @Validated ExperienceDTO experienceDTO,
      BindingResult bindingResult,
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return experienceService.save(userDetails.getUser().getId(), experienceDTO);
  }

  @PutMapping("/update-experience")
  ResponseObject updateExperience(
      @RequestBody @Validated ExperienceDTO experienceDTO,
      BindingResult bindingResult,
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<ExperienceEntity> experience = experienceRepository.findById(experienceDTO.getId());

    if (experience.isEmpty()) {
      throw new CustomException("Experience isn't exists");
    }

    if (experience.get().getCandidateEntity().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this Experience");
    }

    experienceDTO.setCandidateId(userDetails.getUser().getId());

    return experienceService.update(experienceDTO);
  }

  @GetMapping("/delete-experience/{experienceId}")
  ResponseObject deleteExperience(
      @PathVariable(value = "experienceId") long id, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<ExperienceEntity> experience = experienceRepository.findById(id);

    if (experience.isEmpty()) {
      throw new CustomException("Experience isn't exists");
    }

    if (experience.get().getCandidateEntity().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this Experience");
    }

    return experienceService.delete(id);
  }

  @GetMapping("/get-experience")
  ResponseObject getAllExperienceByCandidateId(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return experienceService.getAllExperienceByCandidateId(userDetails.getUser().getId());
  }

  @GetMapping("/get-experience-by-id/{experienceId}")
  ResponseObject getExperienceById(
      @PathVariable(value = "experienceId") long experienceId, Authentication authentication) {

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<ExperienceEntity> experience = experienceRepository.findById(experienceId);

    if (experience.isEmpty()) {
      throw new CustomException("Experience have this id isn't exist");
    }

    if (experience.get().getCandidateEntity().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have right for this Experience");
    }

    return experienceService.getOneById(experienceId);
  }

  @GetMapping("/get-job-post-applied")
  ResponseObject getJobPostAppliedByCandidate(Authentication authentication) {

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return jobPostService.getJobPostAppliedByCandidateId(userDetails.getUser().getId());
  }

  @GetMapping("/get-job-post-saved")
  ResponseObject getJobPostSavedByCandidate(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return jobPostService.getJobPostSavedByCandidateId(userDetails.getUser().getId());
  }

  @GetMapping("/get-application-by-job-post-id-applied/{jobPostId}")
  ResponseObject getApplicationByJobPost(
      Authentication authentication, @PathVariable long jobPostId) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return applicationService.getApplicationByJobPostIdAndCandidateId(
        jobPostId, userDetails.getUser().getId());
  }

  @GetMapping("/get-company-followed")
  ResponseObject getCompanyFollowedByCandidate(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return companyService.getCompanyFollowedByCandidateId(userDetails.getUser().getId());
  }

  @GetMapping("/get-employer-followed")
  ResponseObject getEmployerFollowedByCandidate(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return employerService.getEmployerFollowedByCandidateId(userDetails.getUser().getId());
  }
}
