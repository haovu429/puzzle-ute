package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.EmployerDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.model.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class UserController {

  @Autowired public UserService userService;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired Converter converter;

  @Autowired FilesStorageService storageService;

  @Autowired UserRepository userRepository;

  @Autowired EmployerService employerService;

  @Autowired CandidateService candidateService;

  @Autowired JobPostService jobPostService;

  @Autowired InvoiceService invoiceService;

  @Autowired SubscribeService subscribeService;

  @GetMapping("/user")
  public ResponseObject getAll() {
    return userService.getAll();
  }

  @GetMapping("/user/profile")
  public ResponseObject getProfileAccount(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    UserDTO userDTO = converter.toDTO(userDetails.getUser());

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
    data.put("email_verified", userDTO.isEmailVerified());
    data.put("locale", userDTO.getLocale());

    userDTO.setPassword(null);

    return new ResponseObject(200, "Profile info", data);
  }

  @DeleteMapping("/user/{id}")
  public ResponseObject deleteAccount(@PathVariable Long id) {
    return userService.delete(id);
  }

  @PutMapping("/user")
  public DataResponse updateAccount(
      Authentication authentication, @RequestBody UpdateUserPayload user) {

    // Optional<UserEntity>
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return userService.update(userDetails.getUser().getId(), user);
  }

  //  @GetMapping("/test")
  //  public String update() {
  //    return "OK";
  //  }

  @PostMapping("/upload-avatar")
  public DataResponse uploadAvatar(
      @RequestParam("file") MultipartFile file, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return userService.updateAvatarForUser(userDetails.getUser(), file);
  }
  @GetMapping("/delete-avatar")
  public ResponseObject deleteAvatar(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    String fileName = userDetails.getUser().getEmail() + "_avatar";

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
      // throw new CustomException("Delete image failure, response isn't ok");
    }

    userDetails.getUser().setAvatar(null);

    userRepository.save(userDetails.getUser());

    return new ResponseObject(200, "Delete image success", response);
  }

  @PostMapping("/user/register-employer")
  ResponseObject registerEmployer(
      @RequestBody @Validated EmployerDTO employer,
      BindingResult bindingResult,
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new CustomException(bindingResult.getFieldError().toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    if (userDetails.getUser().getCandidateEntity() != null) {
      throw new CustomException("This account is Candidate!");
    }

    if (userDetails.getUser().getEmployerEntity() != null) {
      throw new CustomException("Info employer for this account was created!");
    }

    employer.setUserId(userDetails.getUser().getId());

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
      Authentication authentication) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    if (userDetails.getUser().getEmployerEntity() != null) {
      throw new CustomException("This account is Employer!");
    }

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

  @GetMapping("/user/get-viewed-job-post-amount")
  DataResponse getAmountApplicationToEmployer(HttpServletRequest request) {

    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromRequest(request);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    return jobPostService.getViewedJobPostAmountByUserId(linkUser.get().getId());
  }

  @GetMapping("/user/view-job-post/{jobPostId}")
  DataResponse getViewJobPost(
      HttpServletRequest request, @PathVariable(value = "jobPostId") long jobPostId) {

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

  @GetMapping("/user/get-one-invoice/{invoiceId}")
  public DataResponse getAllInvoice(Authentication authentication, @PathVariable(value = "invoiceId") long invoiceId) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    InvoiceEntity invoice = invoiceService.getOneInvoice(invoiceId);
    if (!userDetails.getUser().getEmail().equals(invoice.getEmail())) {
      throw new CustomException("You don't have rights for this invoice");
    }
    return new DataResponse(converter.toDTO(invoice));
  }

  @GetMapping("/user/get-subscribed-package")
  public DataResponse getPackageSubscribe(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return subscribeService.getAllSubscriptionsByUserId(userDetails.getUser().getId());
  }
}
