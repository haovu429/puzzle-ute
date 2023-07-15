package hcmute.puzzle.controller;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.InvalidBehaviorException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.dtos.olds.*;
import hcmute.puzzle.infrastructure.dtos.request.ApplicationRequest;
import hcmute.puzzle.infrastructure.dtos.request.CreateExperienceRequest;
import hcmute.puzzle.infrastructure.dtos.request.PostCandidateRequest;
import hcmute.puzzle.infrastructure.dtos.request.UpdateExperienceRequest;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.ExperienceMapper;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.*;
import hcmute.puzzle.services.impl.ApplicationService;
import hcmute.puzzle.services.impl.CurrentUserService;
import hcmute.puzzle.services.impl.ExperienceService;
import hcmute.puzzle.services.impl.JobPostService;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.Utils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/candidate")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

  @Autowired
  ApplicationService applicationService;
  @Autowired
  CandidateService candidateService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  CandidateRepository candidateRepository;

  @Autowired
  JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  ApplicationRepository applicationRepository;

  @Autowired
  JobAlertRepository jobAlertRepository;

  @Autowired JobAlertService jobAlertService;

  @Autowired
  ExperienceService experienceService;

  @Autowired
  ExperienceRepository experienceRepository;

  @Autowired EmployerService employerService;

  @Autowired CompanyService companyService;

  @Autowired
  JobPostService jobPostService;

  @Autowired
  ExperienceMapper experienceMapper;

  @Autowired
  CurrentUserService currentUserService;

  @PersistenceContext
  public EntityManager em;


  @PostMapping("/add")
  DataResponse<CandidateDto> saveCandidate(@RequestBody @Validated CandidateDto candidate,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    if (userDetails.getUser().getCandidate() != null) {
      throw new CustomException("Info candidate for this account was created!");
    }
    candidate.setUserId(userDetails.getUser().getId());

    CandidateDto candidateDto = candidateService.save(candidate);
    return new DataResponse<>(candidateDto);
    //        return new DataResponse(
    //                HttpStatus.OK.value(), "Create candidate successfully", new CandidateDto());
  }

  // Gửi Authentication xác thực tài khoản thì xoá.
  //  @DeleteMapping("/candidate")
  //  DataResponse deleteCandidate() {
  //
  //    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
  //                                                                             .getAuthentication()
  //                                                                             .getPrincipal();
  //    candidateService.delete(userDetails.getUser().getId());
  //    return new DataResponse("Success");
  //  }

  @PutMapping("/update")
  DataResponse<CandidateDto> updateCandidate(
          @RequestBody @Valid PostCandidateRequest candidate, BindingResult bindingResult) {
    //    if (bindingResult.hasErrors()) {
    //      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    //    }
    User crrentUser = currentUserService.getCurrentUser();
    return new DataResponse<>(candidateService.update(crrentUser.getId(), candidate));
  }

  // public
  @GetMapping("/profile")
  DataResponse<CandidateDto> getById() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    CandidateDto candidateDto = candidateService.getOne(userDetails.getUser().getId());
    return new DataResponse<>(candidateDto);
  }

  @GetMapping("/follow-employer/{id}")
  DataResponse<String> followEmployer(@PathVariable(value = "id") Long employerId
          // @RequestBody Map<String, Object> input,
          // @RequestParam(name = "employerId") long employerId,
  ) {
    // Map<String, Object> retMap = new HashMap<String, Object>();

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();

    // long candidateId = linkUser.get().getId();

    // https://stackoverflow.com/questions/58056944/java-lang-integer-cannot-be-cast-to-java-lang-long
    // long candidateId = ((Number) input.get("candidateId")).longValue();
    // long employerId = ((Number) input.get("employerId")).longValue();

    candidateService.followEmployer(userDetails.getUser().getId(), employerId);
    return new DataResponse<>("Success");
  }

  @GetMapping("/cancel-followed-employer/{id}")
  DataResponse<String> cancelFollowEmployer(@PathVariable(value = "id") Long employerId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    candidateService.cancelFollowedEmployer(userDetails.getUser().getId(), employerId);
    return new DataResponse<>("Success");
  }

  @GetMapping("/follow-company/{id}")
  DataResponse<String> followCompany(@PathVariable(value = "id") Long companyId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    candidateService.followCompany(userDetails.getUser().getId(), companyId);
    return new DataResponse<>("Success");
  }

  @GetMapping("/cancel-followed-company/{id}")
  DataResponse<String> cancelFollowCompany(@PathVariable(value = "id") Long companyId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    candidateService.cancelFollowedCompany(userDetails.getUser().getId(), companyId);
    return new DataResponse<>("Success");
  }

  @RequestMapping(path = "/apply-job-post/{postId}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  DataResponse<ApplicationDto> applyJobPost(@PathVariable Long postId, @ModelAttribute ApplicationRequest applicationRequest,
          @RequestPart MultipartFile cvFile) {

    try {
      ApplicationDto applicationDto = applicationService.candidateApply(postId, applicationRequest, cvFile);
      return new DataResponse<>(applicationDto);
    } catch (InvalidBehaviorException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @GetMapping("/cancel-apply-job-post/{postId}")
  DataResponse<String> cancelApplyJobPost(@PathVariable Long postId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    Application application = applicationRepository.findApplicationByCanIdAndJobPostId(userDetails.getUser().getId(),
                                                                                       postId)
                                                   .orElseThrow(() -> new NotFoundDataException(
                                                           "You have not applied this JobPost or JobPost doesn't exist"));

    applicationRepository.delete(application);

    return new DataResponse<>("Success");
  }

  @GetMapping("/save-job-post/{jobPostId}")
  DataResponse<String> saveJobPost(@PathVariable(value = "jobPostId") Long jobPostId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    candidateService.saveJobPost(userDetails.getUser().getId(), jobPostId);
    return new DataResponse<>("Success");
  }

  @GetMapping("/cancel-saved-job-post/{jobPostId}")
  DataResponse<String> cancelSaveJobPost(@PathVariable(value = "jobPostId") Long jobPostId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    candidateService.cancelSavedJobPost(userDetails.getUser().getId(), jobPostId);
    return new DataResponse<>("Success");
  }

  @PostMapping("/add-job-alert")
  DataResponse<JobAlertDto> addJobAlert(@RequestBody @Validated JobAlertDto jobAlertDTO, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    JobAlertDto jobAlertDto = jobAlertService.save(userDetails.getUser().getId(), jobAlertDTO);
    return new DataResponse<>(jobAlertDto);
  }

  @PutMapping("/update-job-alert")
  DataResponse<JobAlertDto> updateJobAlert(@RequestBody @Validated JobAlertDto jobAlertDTO,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();

    JobAlert jobAlert = jobAlertRepository.findById(jobAlertDTO.getId())
                                          .orElseThrow(() -> new NotFoundDataException("Job Alert isn't exists"));

    if (jobAlert.getCandidate().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this JobAlert");
    }

    jobAlertDTO.setCandidateId(userDetails.getUser().getId());
    jobAlertDTO = jobAlertService.update(jobAlertDTO);
    return new DataResponse<>(jobAlertDTO);
  }

  @GetMapping("/delete-job-alert/{jobAlertId}")
  DataResponse<String> deleteJobAlert(@PathVariable(value = "jobAlertId") long id) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();

    JobAlert jobAlert = jobAlertRepository.findById(id)
                                          .orElseThrow(() -> new NotFoundDataException("Job Alert isn't exists"));

    if (jobAlert.getCandidate().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have rights for this JobAlert");
    }
    jobAlertService.delete(id);
    return new DataResponse<>("Success");
  }

  @GetMapping("/get-job-alert")
  DataResponse<List<JobAlertDto>> getAllJobAlertByCandidateId() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    List<JobAlertDto> jobAlertDtos = jobAlertService.getAllJobAlertByCandidateId(userDetails.getUser().getId());
    return new DataResponse<>(jobAlertDtos);
  }

  @GetMapping("/get-job-alert-by-id/{jobAlertId}")
  DataResponse<JobAlertDto> getAllJobAlertById(@PathVariable(value = "jobAlertId") long jobAlertId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();

    JobAlert jobAlert = jobAlertRepository.findById(jobAlertId)
                                          .orElseThrow(() -> new NotFoundDataException(
                                                  "Job Alert have this id isn't exist"));

    if (jobAlert.getCandidate().getId() != userDetails.getUser().getId()) {
      throw new CustomException("You don't have right for this Job Alert");
    }
    JobAlertDto jobAlertDto = jobAlertService.getOneById(jobAlertId);
    return new DataResponse<>(jobAlertDto);
  }

  @PostMapping("/add-experience")
  DataResponse<ExperienceDto> addExperience(@RequestBody @Validated CreateExperienceRequest createExperienceRequest,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    ExperienceDto experienceDto = experienceService.save(userDetails.getUser().getId(), createExperienceRequest);
    return new DataResponse<>(experienceDto);
  }

  @PutMapping("/update-experience")
  DataResponse<ExperienceDto> updateExperience(@RequestBody @Validated ExperienceDto experienceDTO,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).toString());
    }
    UpdateExperienceRequest updateExperienceRequest = experienceMapper.experienceDtoToUpdateExperienceRequest(experienceDTO);
    ExperienceDto experienceDto = experienceService.update(experienceDTO.getCandidateId(), updateExperienceRequest);
    return new DataResponse<>(experienceDto);
  }

  @GetMapping("/delete-experience/{experienceId}")
  DataResponse<String> deleteExperience(@PathVariable(value = "experienceId") long id) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();

    Experience experience = experienceRepository.findById(id)
                                                .orElseThrow(
                                                        () -> new NotFoundDataException("Experience isn't exists"));

    if (experience.getCandidate().getId() != userDetails.getUser().getId()) {
      throw new UnauthorizedException("You don't have rights for this Experience");
    }
    experienceService.delete(id);
    return new DataResponse<>("Success");
  }

  @GetMapping("/get-experience")
  DataResponse<List<ExperienceDto>> getAllExperienceByCandidateId() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    List<ExperienceDto> experiences = experienceService.getAllExperienceByCandidateId(userDetails.getUser().getId());
    return new DataResponse<>(experiences);
  }

  @GetMapping("/get-experience-by-id/{experienceId}")
  DataResponse<ExperienceDto> getExperienceById(@PathVariable(value = "experienceId") long experienceId) {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    Experience experience = experienceRepository.findById(experienceId)
                                                .orElseThrow(() -> new NotFoundDataException(
                                                        "Experience have this id isn't exist"));

    if (experience.getCandidate().getId() != userDetails.getUser().getId()) {
      throw new UnauthorizedException("You don't have right for this Experience");
    }
    ExperienceDto experienceDto = experienceService.getOneById(experienceId);
    return new DataResponse<>(experienceDto);
  }

  @GetMapping("/get-job-post-applied")
  DataResponse<List<JobPostDto>> getJobPostAppliedByCandidate() {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    List<JobPostDto> jobPostDtos = jobPostService.getJobPostAppliedByCandidateId(userDetails.getUser().getId());

    return new DataResponse<>(jobPostDtos);
  }

  @GetMapping("/get-job-post-saved")
  DataResponse<List<JobPostDto>> getJobPostSavedByCandidate() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    List<JobPostDto> jobPostDtos = jobPostService.getJobPostSavedByCandidateId(userDetails.getUser().getId());
    return new DataResponse<>(jobPostDtos);
  }

  @GetMapping("/get-application-by-job-post-id-applied/{jobPostId}")
  DataResponse<ApplicationDto> getApplicationByJobPost(@PathVariable long jobPostId) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    ApplicationDto applicationDto = applicationService.getApplicationByJobPostIdAndCandidateId(jobPostId,
                                                                                               userDetails.getUser()
                                                                                                          .getId());
    return new DataResponse<>(applicationDto);
  }

  @GetMapping("/application/applied")
  DataResponse<Page<ApplicationDto>> getApplicationApplied(@RequestParam(value = "page", required = false) Integer page,
          @RequestParam(required = false) Integer size) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    Pageable pageable = Utils.getPageable(page, size);
    Page<ApplicationDto> applicationDtos = applicationService.getApplicationApplied(userDetails.getUser().getId(),
                                                                                    pageable);
    return new DataResponse<>(applicationDtos);
  }

  @GetMapping("/application/applied/detail/{applicationId}")
  DataResponse<ApplicationDto> getApplicationApplied(@PathVariable Long applicationId) {
    ApplicationDto applicationDtos = applicationService.getDetailApplicationApplied(applicationId);
    return new DataResponse<>(applicationDtos);
  }


  @GetMapping("/get-company-followed")
  DataResponse<List<CompanyResponse>> getCompanyFollowedByCandidate() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    List<CompanyResponse> companyResponses = companyService.getCompanyFollowedByCandidateId(
            userDetails.getUser().getId());
    return new DataResponse<>(companyResponses);
  }

  @GetMapping("/get-employer-followed")
  DataResponse<List<EmployerDto>> getEmployerFollowedByCandidate() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                             .getAuthentication()
                                                                             .getPrincipal();
    List<EmployerDto> employerDtos = employerService.getEmployerFollowedByCandidateId(userDetails.getUser().getId());
    return new DataResponse<>(employerDtos);
  }

  @GetMapping("/job-alert/suggest")
  DataResponse<Page<JobPostDto>> suggestJobPostByJobAlert(@RequestParam Long jobAlertId, @RequestParam(value = "page", required = false) Integer page,
          @RequestParam(required = false) Integer size) {
    Pageable pageable = Utils.getPageable(page, size);
    JobAlert jobAlert = jobAlertRepository.findById(jobAlertId).orElseThrow(
            () -> new NotFoundDataException("Not found job alert")
    );
    Page<JobPostDto> jobAlertDtoPage  = jobPostService.filterJobPostByJobAlert(jobAlert, pageable);
    return new DataResponse<>(jobAlertDtoPage);
  }

  @GetMapping("/application")
  DataResponse<ApplicationDto> responseApplicationByCandidateIdAndJobPostId(@RequestParam Long jobPostId) {
    JobPost jobPost = jobPostRepository.findById(jobPostId)
                                       .orElseThrow(() -> new NotFoundDataException("Not found job post "));

    CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                           .getAuthentication()
                                                                           .getPrincipal();
    User currentUser = principal.getUser();

    ApplicationDto applicationDto = applicationService.getApplicationByJobPostIdAndCandidateId(jobPostId,
                                                                                               currentUser.getId());
    if (currentUser.getId() != applicationDto.getCandidateId()) {
      throw new UnauthorizedException("You don't have right for this application");
    }
    return new DataResponse<>(applicationDto);
  }
}
