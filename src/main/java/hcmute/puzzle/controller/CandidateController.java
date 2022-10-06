package hcmute.puzzle.controller;

import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
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
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
public class CandidateController {

  @Autowired CandidateService candidateService;

  @Autowired UserRepository userRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @PostMapping("/candidate/add")
  ResponseObject save(
      @RequestBody @Validated CandidateDTO candidate,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization", required = true) String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }

    Optional<UserEntity> linkUser = checkIdentity(token, candidate.getUserId());
    // String validUserEmail;
    // Check user of current account is updating info
    // JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
    //    linkUser = userRepository.findById(candidate.getUserId());
    //    if (linkUser.isEmpty()) {
    //      throw new RuntimeException("Not found userId = " + candidate.getUserId());
    //    }
    //    validUserEmail = jwtAuthenticationFilter.checkToken(token);
    //    System.out.println("validUserEmail: " + validUserEmail );
    //    System.out.println("linkUser: " + linkUser.get().getEmail() );
    //    if (!linkUser.get().getEmail().equals(validUserEmail)) {
    //      throw new RuntimeException("It is not allowed to edit other users' information");
    //    }

    // Cách khác: lấy thông tin id từ token và gán cho thông tin cần sửa, không để người dùng input
    // userId.
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

  @DeleteMapping("/candidate/{id}")
  ResponseObject delete(@PathVariable Long id) {
    return candidateService.delete(id);
  }

  @PutMapping("/candidate/update")
  ResponseObject update(
      @RequestBody @Validated CandidateDTO candidate,
      BindingResult bindingResult,
      @RequestHeader(value = "Authorization", required = true) String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(bindingResult.getFieldError().toString());
    }
    Optional<UserEntity> linkUser = checkIdentity(token, candidate.getUserId());
    candidate.setUserId(linkUser.get().getId());
    candidate.setId(linkUser.get().getId());
    return candidateService.update(candidate);
  }

  @GetMapping("/candidate/{id}")
  ResponseObject getById(@PathVariable Long id) {
    return candidateService.getOne(id);
  }

  @PostMapping("/candidate/follow-employer")
  ResponseEntity<Map<String, Object>> followEmployer(@RequestBody Map<String, Object> input) {
    Map<String, Object> retMap = new HashMap<String, Object>();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    long candidateId = ((Number) input.get("candidateId")).longValue();
    long employerId = ((Number) input.get("employerId")).longValue();

    candidateService.followEmployer(candidateId, employerId);

    ResponseEntity<Map<String, Object>> retValue =
        new ResponseEntity<Map<String, Object>>(retMap, HttpStatus.OK);
    return retValue;
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
