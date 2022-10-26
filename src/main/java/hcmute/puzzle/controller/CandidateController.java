package hcmute.puzzle.controller;

import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.ApplicationEntity;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(value = "http://localhost:3000")
public class CandidateController {

  @Autowired CandidateService candidateService;

  @Autowired UserRepository userRepository;

  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired CandidateRepository candidateRepository;
  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  ApplicationRepository applicationRepository;

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
  @GetMapping("/candidate/{id}")
  ResponseObject getById(@PathVariable long id) {
    return candidateService.getOne(id);
  }

  @PostMapping("/candidate/follow-employer")
  ResponseEntity<Map<String, Object>> followEmployer(
      @RequestBody Map<String, Object> input,
      // @RequestParam(name = "employerId") long employerId,
      @RequestHeader(value = "Authorization", required = true) String token) {
    Map<String, Object> retMap = new HashMap<String, Object>();

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
    // long candidateId = linkUser.get().getId();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    // long candidateId = ((Number) input.get("candidateId")).longValue();
    long employerId = ((Number) input.get("employerId")).longValue();

    candidateService.followEmployer(linkUser.get().getId(), employerId);

    ResponseEntity<Map<String, Object>> retValue =
        new ResponseEntity<Map<String, Object>>(retMap, HttpStatus.OK);
    return retValue;
  }

  @GetMapping("/candidate/apply-job-post/{postId}")
  ResponseObject applyJobPost(
          @PathVariable Long postId, @RequestHeader(value = "Authorization") String token) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.get().getCandidateEntity() == null) {
      throw new CustomException("This account isn't Candidate");
    }

    Optional<CandidateEntity> candidate = candidateRepository.findById(linkUser.get().getCandidateEntity().getId());
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(postId);
    //    if (candidate.isEmpty()) {
    //      throw new NoSuchElementException("Candidate no value present");
    //    }

    if (jobPost.isEmpty()) {
      throw new NoSuchElementException("Employer no value present");
    }

    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setCandidateEntity(linkUser.get().getCandidateEntity());
    applicationEntity.setJobPostEntity(jobPost.get());
    applicationRepository.save(applicationEntity);

    return new ResponseObject(200, "Apply success", null);
  }
}
