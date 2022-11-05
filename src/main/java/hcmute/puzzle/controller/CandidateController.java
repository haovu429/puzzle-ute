package hcmute.puzzle.controller;

import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.JobAlertDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.*;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.*;
import hcmute.puzzle.services.CandidateService;
import hcmute.puzzle.services.JobAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(value = "http://localhost:3000")
public class CandidateController {

  @Autowired CandidateService candidateService;

  @Autowired UserRepository userRepository;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired CandidateRepository candidateRepository;
  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired ApplicationRepository applicationRepository;

  @Autowired JobAlertRepository jobAlertRepository;

  @Autowired
  JobAlertService jobAlertService;

  @PostMapping("/candidate/add")
  ResponseObject save(
      @RequestBody @Validated CandidateDTO candidate,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization", required = true) String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    if (linkUser.get().getEmployerEntity() != null) {
      throw new CustomException("This account is Employer!");
    }

    if (linkUser.get().getCandidateEntity() != null) {
      throw new CustomException("Info candidate for this account was created!");
    }
    candidate.setUserId(linkUser.get().getId());

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
  ResponseObject delete(
      @PathVariable long id,
      @RequestHeader(value = "Authorization", required = true) String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    return candidateService.delete(linkUser.get().getCandidateEntity().getId());
  }

  @PutMapping("/candidate/update")
  ResponseObject update(
      @RequestBody @Validated CandidateDTO candidate,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization", required = true) String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    candidate.setUserId(linkUser.get().getId());
    candidate.setId(linkUser.get().getId());
    return candidateService.update(candidate);
  }

  // public
  @GetMapping("/candidate/get-one/{id}")
  ResponseObject getById(@PathVariable long id) {
    return candidateService.getOne(id);
  }

  @GetMapping("/candidate/follow-employer/{id}")
  ResponseObject followEmployer(
      @PathVariable(value = "id") Long employerId,
      // @RequestBody Map<String, Object> input,
      // @RequestParam(name = "employerId") long employerId,
      @RequestHeader(value = "Authorization", required = true) String token) {
    // Map<String, Object> retMap = new HashMap<String, Object>();

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // long candidateId = linkUser.get().getId();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    // long candidateId = ((Number) input.get("candidateId")).longValue();
    // long employerId = ((Number) input.get("employerId")).longValue();

    return candidateService.followEmployer(linkUser.get().getId(), employerId);
  }

  @GetMapping("/candidate/follow-company/{id}")
  ResponseObject followCompany(
      @PathVariable(value = "id") Long companyId,
      // @RequestParam(name = "employerId") long employerId,
      @RequestHeader(value = "Authorization", required = true) String token) {
    // Map<String, Object> retMap = new HashMap<String, Object>();

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // long candidateId = linkUser.get().getId();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    // long candidateId = ((Number) input.get("candidateId")).longValue();

    return candidateService.followCompany(linkUser.get().getId(), companyId);
  }

  @GetMapping("/candidate/apply-job-post/{postId}")
  ResponseObject applyJobPost(
      @PathVariable Long postId, @RequestHeader(value = "Authorization") String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.get().getCandidateEntity() == null) {
      throw new CustomException("This account isn't Candidate");
    }

    Optional<CandidateEntity> candidate =
        candidateRepository.findById(linkUser.get().getCandidateEntity().getId());
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(postId);
    //    if (candidate.isEmpty()) {
    //      throw new NoSuchElementException("Candidate no value present");
    //    }

    if (jobPost.isEmpty()) {
      throw new NoSuchElementException("JobPost no value present");
    }

    if (!jobPost.get().isActive()) {
      throw new CustomException("You can't apply this jobPost. It isn't active");
    }

    Set<ApplicationEntity> applications = applicationRepository.findApplicationByCanIdAndJobPostId(linkUser.get().getId(), postId);
    if (!applications.isEmpty()) {
      throw new CustomException("You applied for this job");
    }

    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setCandidateEntity(linkUser.get().getCandidateEntity());
    applicationEntity.setJobPostEntity(jobPost.get());
    applicationRepository.save(applicationEntity);

    return new ResponseObject(200, "Apply success", null);
  }

  @GetMapping("/candidate/cancel-apply-job-post/{postId}")
  ResponseObject cancelApplyJobPost(
          @PathVariable Long postId, @RequestHeader(value = "Authorization") String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.get().getCandidateEntity() == null) {
      throw new CustomException("This account isn't Candidate");
    }

    Set<ApplicationEntity> applications = applicationRepository.findApplicationByCanIdAndJobPostId(linkUser.get().getId(), postId);
    if (applications.isEmpty()) {
      throw new CustomException("You have not applied this JobPost or JobPost doesn't exist");
    }
    applicationRepository.deleteAll(applications);

    return new ResponseObject(200, "Cancel apply success", null);
  }

  @GetMapping("/candidate/save-job-post/{jobPostId}")
  ResponseObject saveJobPost(
          @PathVariable(value = "jobPostId") Long jobPostId,
          // @RequestParam(name = "employerId") long employerId,
          @RequestHeader(value = "Authorization", required = true) String token) {
    // Map<String, Object> retMap = new HashMap<String, Object>();

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // long candidateId = linkUser.get().getId();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    // long candidateId = ((Number) input.get("candidateId")).longValue();

    return candidateService.saveJobPost(linkUser.get().getId(), jobPostId);
  }

  @PostMapping("/candidate/add-job-alert")
  ResponseObject addJobAlert(
          @RequestBody @Validated JobAlertDTO jobAlertDTO,
          BindingResult bindingResult,
          @RequestHeader(value = "Authorization") String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    if (linkUser.get().getEmployerEntity() != null || linkUser.get().getCandidateEntity() == null) {
      throw new CustomException("This account isn't Candidate!");
    }

    return jobAlertService.save(linkUser.get().getId(), jobAlertDTO);
  }

  @PutMapping("/candidate/update-job-alert")
  ResponseObject updateJobAlert(@RequestBody @Validated JobAlertDTO jobAlertDTO,
                                BindingResult bindingResult,
                                @RequestHeader(value = "Authorization") String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    Optional<JobAlertEntity> jobAlert = jobAlertRepository.findById(jobAlertDTO.getId());

    if (jobAlert.isEmpty()) {
      throw new CustomException("Job Alert isn't exists");
    }

    if (jobAlert.get().getCandidateEntity().getId() != linkUser.get().getId()) {
      throw new CustomException("You don't have rights for this JobAlert");
    }

    jobAlertDTO.setCandidateId(linkUser.get().getId());

    return jobAlertService.update(jobAlertDTO);
  }

  @GetMapping ("/candidate/delete-job-alert/{jobAlertId}")
  ResponseObject deleteJobAlert(@PathVariable(value = "jobAlertId") long id,
                                @RequestHeader(value = "Authorization") String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);



    Optional<JobAlertEntity> jobAlert = jobAlertRepository.findById(id);

    if (jobAlert.isEmpty()) {
      throw new CustomException("Job Alert isn't exists");
    }

    if (jobAlert.get().getCandidateEntity().getId() != linkUser.get().getId()) {
      throw new CustomException("You don't have rights for this JobAlert");
    }

    return jobAlertService.delete(id);
  }

  @GetMapping ("/candidate/get-job-alert")
  ResponseObject getAllJobAlertByCandidateId(@RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    return jobAlertService.getAllJobAlertByCandidateId(linkUser.get().getId());
  }

  @GetMapping ("/candidate/get-job-alert-by-id/{jobAlertId}")
  ResponseObject getAllJobAlertById(@PathVariable(value = "jobAlertId") long jobAlertId, @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    Optional<JobAlertEntity> jobAlert = jobAlertRepository.findById(jobAlertId);

    if (jobAlert.isEmpty()) {
      throw new CustomException("Job Alert have this id isn't exist");
    }

    if (jobAlert.get().getCandidateEntity().getId() != linkUser.get().getId()) {
      throw new CustomException("You don't have right for this Job Alert");
    }

    return jobAlertService.getOneById(jobAlertId);
  }

}
