package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.EmployerService;
import hcmute.puzzle.services.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
public class EmployerController {
  @Autowired EmployerService employerService;

  @Autowired UserRepository userRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired JobPostService jobPostService;

  @Autowired Converter converter;

  @PostMapping("/employer/add")
  ResponseObject save(
      @RequestBody @Validated EmployerDTO employer,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization", required = true) String token) {
    if (bindingResult.hasErrors()) {
      throw new CustomException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.get().getCandidateEntity() != null) {
      throw new CustomException("This account is Candidate!");
    }

    if (linkUser.get().getEmployerEntity() != null) {
      throw new CustomException("Info employer for this account was created!");
    }

    employer.setUserId(linkUser.get().getId());

    Optional<EmployerDTO> employerDTO = employerService.save(employer);
    if (employerDTO.isPresent()) {
      return new ResponseObject(
          HttpStatus.OK.value(), "Create employer successfully", employerDTO.get());
    } else {
      throw new CustomException("Add employer failed");
    }

    //    return new ResponseObject(
    //        HttpStatus.OK.value(), "Create employer successfully", new EmployerDTO());
  }

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

  @GetMapping("/employer/{id}")
  ResponseObject getById(@PathVariable Long id) {
    return employerService.getOne(id);
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
}