package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.TimeUtil;
import hcmute.puzzle.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class UserController {

  @Autowired public UserService userService;

  @Autowired
  JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  Converter converter;

  @Autowired
  FilesStorageService storageService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  EmployerService employerService;

  @Autowired
  CandidateService candidateService;

  @Autowired
  JobPostService jobPostService;

  @Autowired
  InvoiceService invoiceService;

  @GetMapping("/user")
  public ResponseObject getAll() {
    return userService.getAll();
  }

  @GetMapping("/user/profile")
  public ResponseObject getOne(@RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    UserDTO userDTO = converter.toDTO(linkUser.get());

    Map<String, Object> data = new HashMap<>();
    data.put("username", userDTO.getUsername());
    data.put("email", userDTO.getEmail());
    data.put("phone", userDTO.getPhone());
    data.put("avatar", userDTO.getAvatar());

    String joinDate = TimeUtil.dateToString(userDTO.getJoinDate(), TimeUtil.FORMAT_DATE);
    data.put("joinDate", joinDate);

    data.put("fullName", userDTO.getFullName());
    data.put("roleCodes", userDTO.getRoleCodes());
    data.put("active", userDTO.isActive());

    userDTO.setPassword(null);

    return new ResponseObject(200, "Profile info", data);
  }

  @DeleteMapping("/user/{id}")
  public ResponseObject delete(@PathVariable Long id) {
    return userService.delete(id);
  }

  @PutMapping("/user/{id}")
  public ResponseObject update(@RequestHeader(value = "Authorization") String token, @RequestBody UserDTO user) {

    //Optional<UserEntity>
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }
    return userService.update(linkUser.get().getId(), user);
  }

  @GetMapping("/test")
  public String update() {
    return "OK";
  }

  @PostMapping("/upload-avatar")
  public ResponseObject uploadFile(
          @RequestParam("file") MultipartFile file,
          @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    String fileName = linkUser.get().getEmail() + "_avatar";

    Map response = null;

    try {
      // push to storage cloud
      response = storageService.uploadAvatarImage(fileName, file);

    } catch (Exception e) {
      e.printStackTrace();
    }

    if (response == null) {
      throw new CustomException("Upload image failure");
    }

    if (response.get("secure_url") == null) {
      throw new CustomException("Can't get url from response of storage cloud");
    }

    String url = response.get("secure_url").toString();

    linkUser.get().setAvatar(url);

    userRepository.save(linkUser.get());

    return new ResponseObject(200, "Upload image success", response);
  }

  @GetMapping("/delete-avatar")
  public ResponseObject deleteFile(
          @RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    String fileName = linkUser.get().getEmail() + "_avatar";

    Map response;

    response = storageService.deleteAvatarImage(fileName);

    if (response == null) {
      throw new CustomException("Delete image failure, not response");
    }

    if (response.get("result") == null) {
      throw new CustomException("Can't get url from response of storage cloud");
    }

    String result = response.get("result").toString();

    if (!result.equals("ok")) {
      return new ResponseObject(200, "Delete image failure, response isn't ok", response);
      //throw new CustomException("Delete image failure, response isn't ok");
    }

    linkUser.get().setAvatar(null);

    userRepository.save(linkUser.get());

    return new ResponseObject(200, "Delete image success", response);
  }

  @PostMapping("/user/register-employer")
  ResponseObject registerEmployer(
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

  @PostMapping("/user/register-candidate")
  ResponseObject registerCandidate(
          @RequestBody @Validated CandidateDTO candidate,
          BindingResult bindingResult,
          @RequestHeader(value = "Authorization") String token) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

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

  @GetMapping("/user/get-viewed-job-post-amount")
  DataResponse getAmountApplicationToEmployer(HttpServletRequest request) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromRequest(request);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    return jobPostService.getViewedJobPostAmountByUserId(linkUser.get().getId());
  }

  @GetMapping("/user/view-job-post/{jobPostId}")
  DataResponse getViewJobPost(HttpServletRequest request, @PathVariable(value = "jobPostId") long jobPostId) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromRequest(request);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    return jobPostService.viewJobPost(linkUser.get().getId(), jobPostId);
  }

  @GetMapping("/user/get-invoice")
  public DataResponse getInvoiceForUser(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return invoiceService.getInvoiceByEmailUser(userDetails.getUser().getEmail());
  }

  @GetMapping("/user/get-subscribed_package")
  public DataResponse getPackageSubscribe(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return new DataResponse(userDetails.getUser().getSubscribeEntities());
  }

}
