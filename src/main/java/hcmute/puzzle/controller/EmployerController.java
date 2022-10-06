package hcmute.puzzle.controller;

import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.EmployerService;
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

  @PostMapping("/employer/add")
  ResponseObject save(
      @RequestBody @Validated EmployerDTO employer,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization", required = true) String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = checkIdentity(token, employer.getUserId());
    employer.setUserId(linkUser.get().getId());


    Optional<EmployerDTO> employerDTO = employerService.save(employer);
    if (employerDTO.isPresent()) {
      return new ResponseObject(
          HttpStatus.OK.value(), "Create employer successfully", employerDTO.get());
    } else {
      throw new RuntimeException("Add employer failed");
    }

    //    return new ResponseObject(
    //        HttpStatus.OK.value(), "Create employer successfully", new EmployerDTO());
  }

  @DeleteMapping("/employer/{id}")
  ResponseObject delete(@PathVariable Long id) {
    return employerService.delete(id);
  }

  @PutMapping("/employer/update")
  ResponseObject update(
      @RequestBody @Validated EmployerDTO employer,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization", required = true) String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = checkIdentity(token, employer.getUserId());
    employer.setUserId(linkUser.get().getId());
    employer.setId(linkUser.get().getId());

    return employerService.update(employer);
  }

  @GetMapping("/employer/{id}")
  ResponseObject getById(@PathVariable Long id) {
    return employerService.getOne(id);
  }

  private Optional<UserEntity> checkIdentity(String token, long userIdInput) {
    Optional<UserEntity> linkUser;
    String validUserEmail;

    validUserEmail = jwtAuthenticationFilter.checkToken(token);
    linkUser = userRepository.findByEmail(validUserEmail);
    if (linkUser.isEmpty()) {
      throw new RuntimeException("Not found userId = " + userIdInput);
    }
    return linkUser;
  }
}
