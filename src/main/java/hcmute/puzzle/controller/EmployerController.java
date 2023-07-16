package hcmute.puzzle.controller;

import com.detectlanguage.errors.APIError;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.hirize.model.AIMatcherData;
import hcmute.puzzle.hirize.model.HirizeIQData;
import hcmute.puzzle.hirize.model.HirizeResponse;
import hcmute.puzzle.hirize.service.HirizeService;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.request.CreateCompanyRoleUserRequest;
import hcmute.puzzle.infrastructure.dtos.request.JobPostUserPostRequest;
import hcmute.puzzle.infrastructure.dtos.response.CandidateApplicationResult;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.Company;
import hcmute.puzzle.infrastructure.entities.JobPost;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.CompanyMapper;
import hcmute.puzzle.infrastructure.models.ApplicationResult;
import hcmute.puzzle.infrastructure.models.JobPostWithApplicationAmount;
import hcmute.puzzle.infrastructure.models.ResponseApplication;
import hcmute.puzzle.infrastructure.models.enums.FileType;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.CompanyRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.services.impl.ApplicationService;
import hcmute.puzzle.services.impl.EmployerService;
import hcmute.puzzle.services.impl.JobPostService;
import hcmute.puzzle.utils.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static hcmute.puzzle.utils.Constant.SUFFIX_COMPANY_IMAGE_FILE_NAME;

@Log4j2
@RestController
@RequestMapping(path = "/employer")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
@SecurityRequirement(name = "bearerAuth")
public class EmployerController {

	@Autowired
	EmployerService employerService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	JobPostRepository jobPostRepository;

	@Autowired
	JobPostService jobPostService;

	@Autowired
	Converter converter;

	@Autowired
	CompanyService companyService;

	@Autowired
	ApplicationRepository applicationRepository;

	@Autowired
	ApplicationService applicationService;

	@Autowired
	FilesStorageService storageService;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	CompanyMapper companyMapper;

	@Autowired
	HirizeService hirizeService;

	// Delete by user Entity
	//  @DeleteMapping("/deactivate")
	//  DataResponse deleteEmployer(Authentication authentication) {
	//    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	//    return employerService.delete(userDetails.getUser().getId());
	//  }

	@PutMapping("/update")
	DataResponse<EmployerDto> updateEmployer(@RequestBody @Validated EmployerDto employer,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new CustomException(bindingResult.getFieldError().toString());
		}
		EmployerDto employerDto = employerService.update(employer);
		return new DataResponse<>(employerDto);
	}

	@GetMapping("/profile")
	DataResponse<EmployerDto> getById() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		EmployerDto employerDto = employerService.getOne(userDetails.getUser().getId());
		return new DataResponse<>(employerDto);
	}

	@PostMapping("/post-job")
	DataResponse<JobPostDto> createJobPost(@RequestBody @Validated JobPostUserPostRequest createJobPostRequest) {
		// Validate JobPost
		jobPostService.validateJobPost(createJobPostRequest);
		//jobPostService.checkCreatedJobPostLimit(userDetails.getUser().getId());
		JobPostDto jobPostDto = jobPostService.add(createJobPostRequest);
		return new DataResponse<>(jobPostDto);
	}

	@DeleteMapping("/delete-job-post/{id}")
	DataResponse<String> deleteJobPost(@PathVariable Long id, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		// employer can not change active status
		JobPost jobPost = jobPostRepository.findById(id)
										   .orElseThrow(() -> new NotFoundDataException("Job post isn't exists"));


		if (jobPost.getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this jobPost");
		}
		jobPostService.markJobPostWasDelete(id);
		return new DataResponse<>("Success");
	}

	@PutMapping("/update-job-post/{jobPostId}")
	DataResponse<JobPostDto> updateJobPost(@RequestBody @Validated JobPostUserPostRequest jobPostUserPostRequest,
			@PathVariable(required = false) Long jobPostId) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		// Validate JobPost
		// jobPostService.validateJobPost(jobPostDTO);
		JobPostDto jobPostDto = jobPostService.updateJobPostWithRoleUser(jobPostId, jobPostUserPostRequest);

		return new DataResponse<>(jobPostDto);
	}

	//  void pipelineCheckRights(long jobPostId, Optional<UserEntity> linkUser) {
	//
	//    // Check is Employer
	//    if (linkUser.get().getEmployerEntity() == null) {
	//      throw new CustomException("This account isn't Employer");
	//    }
	//
	//    Optional<JobPostEntity> jobPostEntity = jobPostRepository.findById(jobPostId);
	//    // Check job post exists
	//    if (jobPostEntity.isEmpty()) {
	//      throw new CustomException("Job post isn't exists!");
	//    }
	//
	//    // Check employer update is created employer.
	//    if (jobPostEntity.get().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
	//      throw new CustomException("You have no rights to this post!");
	//    }
	//  }

	@PostMapping("/create-info-company")
	public DataResponse<CompanyResponse> saveCompany(@ModelAttribute CreateCompanyRoleUserRequest companyPayload) throws
			NotFoundException {
		//    if (companyPayload.getImageFile().getSize() > 5 * 1024 * 1024) {
		//      throw new IllegalArgumentException("File size exceeds the limit 5MB");
		//    }
		CompanyDto companyDto = companyMapper.createCompanyRoleUserRequestToCompanyDto(companyPayload);
		CompanyResponse companyResponse = companyService.save(companyDto);
		return new DataResponse<>(companyResponse);
	}

	// Lấy danh sách ứng viên đã apply vào một jobPost
	@GetMapping("/candidate-apply-jobpost/{jobPostId}")
	DataResponse<List<CandidateDto>> getCandidateApplyJobPost(@PathVariable Long jobPostId) {

		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();

		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("JobPost isn't exist"));

		if (jobPost.getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this jobPost");
		}

		List<CandidateDto> candidateDtos = jobPostService.getCandidatesApplyJobPost(jobPostId);
		return new DataResponse<>(candidateDtos);
	}

	@PostMapping("/response-application/{applicationId}")
	DataResponse<String> responseApplication(@PathVariable long applicationId,
			@RequestBody ApplicationResult applicationResult) {
		applicationService.responseApplication(applicationId, applicationResult);
		return new DataResponse<>("Success");
	}

	@PostMapping("/response-application-by-candidate-and-job-post")
	DataResponse<String> responseApplicationV2(@RequestBody ResponseApplication responseApplication) {
		applicationService.responseApplicationByCandidateAndJobPost(responseApplication);
		return new DataResponse<>("Success");
	}

	@GetMapping("/application")
	DataResponse<ApplicationDto> responseApplicationByCandidateIdAndJobPostId(@RequestParam Long jobPostId,
			@RequestParam Long candidateId) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post "));

		CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext()
																			   .getAuthentication()
																			   .getPrincipal();
		User currentUser = principal.getUser();
		if (currentUser.getId() != jobPost.getCreatedEmployer().getId()) {
			throw new UnauthorizedException("You don't have right for this application");
		}
		ApplicationDto applicationDto = applicationService.getApplicationByJobPostIdAndCandidateId(jobPostId,
																								   candidateId);
		return new DataResponse<>(applicationDto);
	}

	@GetMapping("/get-all-job-post-created")
	DataResponse<Page<JobPostWithApplicationAmount>> getJobPostCreated(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = this.getPageable(page, size);
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		Page<JobPostWithApplicationAmount> jobPostWithApplicationAmounts = jobPostService.getJobPostCreatedByEmployerId(
				userDetails.getUser().getId(), pageable);
		return new DataResponse<>(jobPostWithApplicationAmounts);
	}

	private Pageable getPageable(Integer page, Integer size) {
		Pageable pageable = Pageable.unpaged();
		if (page != null && size != null) {
			pageable = Pageable.ofSize(size).withPage(page);
		}
		return pageable;
	}

	@GetMapping("/get-all-job-post-created-active")
	DataResponse<List<JobPostDto>> getJobPostCreatedActive() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		List<JobPostDto> jobPostDtos = jobPostService.getJobPostByCreateEmployerId(userDetails.getUser().getId(), true);
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/get-all-job-post-created-inactive")
	DataResponse<List<JobPostDto>> getJobPostCreatedInactive() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		List<JobPostDto> jobPostDtos = jobPostService.getJobPostByCreateEmployerId(userDetails.getUser().getId(),
																				   false);
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/get-application-by-job-post/{jobPostId}")
	DataResponse<Page<ApplicationDto>> getApplicationByJobPost(@PathVariable long jobPostId,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = this.getPageable(page, size);
		Page<ApplicationDto> applications = applicationService.getApplicationByJobPostId(jobPostId, pageable);
		return new DataResponse<>(applications);
	}

	@GetMapping("/get-candidate-and-result-by-job-post/{jobPostId}")
	DataResponse<Page<CandidateApplicationResult>> getCandidateAndApplicationResultByJobPost(
			@PathVariable long jobPostId, Pageable pageable) {
		Page<CandidateApplicationResult> candidateAppliedAndResults = applicationService.getCandidateAppliedToJobPostIdAndResult(
				jobPostId, pageable);
		return new DataResponse<>(candidateAppliedAndResults);
	}

	@GetMapping("/get-all-candidate-and-result-to-employer")
	DataResponse<List<CandidateApplicationResult>> getCandidateAndApplicationResultByEmployerId() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		List<CandidateApplicationResult> candidateApplicationResults = applicationService.getCandidateAppliedToEmployerAndResult(
				userDetails.getUser().getId());
		return new DataResponse<>(candidateApplicationResults);
	}

	// This API get amount application to one Employer by employer id
	// , from all job post which this employer created
	@GetMapping("/get-amount-application-to-employer")
	DataResponse<Long> getAmountApplicationToEmployer(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		Long amount = applicationService.getAmountApplicationToEmployer(userDetails.getUser().getId());
		return new DataResponse<>(amount);
	}

	@GetMapping("/get-application-rate-of-job-post/{jobPostId}")
	DataResponse<Double> getApplicationRateOfJobPost(@PathVariable(value = "jobPostId") long jobPostId) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		// Check have rights
		Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
		if (jobPost.isEmpty()) {
			throw new CustomException("Job post isn't exists");
		}

		if (jobPost.get().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this job post");
		}

		return new DataResponse<>(jobPostService.getApplicationRateByJobPostId(jobPostId));
	} // getApplicationRateEmployerId

	@GetMapping("/get-application-rate-of-employer")
	DataResponse<Double> getApplicationRateOfEmployer() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		return new DataResponse<>(employerService.getApplicationRateEmployerId(userDetails.getUser().getId()));
	}

	@GetMapping("/get-total-job-post-view-of-employer")
	DataResponse<Long> getTotalJobPostViewOfEmployer() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		long total = jobPostService.getTotalJobPostViewOfEmployer(userDetails.getUser().getId());
		return new DataResponse<>(total);
	}

	@GetMapping("/get-limit-num-of-job-post-created")
	DataResponse<Long> getLimitNumOfJobPostCreated() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		long limit = jobPostService.getLimitNumberOfJobPostsCreatedForEmployer(userDetails.getUser().getId());
		return new DataResponse<>(limit);
	}

	@GetMapping("/get-created-company")
	DataResponse<List<CompanyResponse>> getCreatedCompany() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		List<CompanyResponse> companyResponses = companyService.getCreatedCompanyByEmployerId(
				userDetails.getUser().getId());
		return new DataResponse<>(companyResponses);
	}

	@PostMapping("/upload-image-company/{companyId}")
	public DataResponse<CompanyDto> uploadCompanyImage(@PathVariable(value = "companyId") long companyId,
			@RequestParam("file") MultipartFile file) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();

		Company company = companyRepository.findById(companyId)
										   .orElseThrow(() -> new NotFoundDataException("Company isn't exists"));
		if (company.getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new UnauthorizedException("You don't have rights for this company");
		}
		String fileName = String.format("%s_%s", company.getId(), SUFFIX_COMPANY_IMAGE_FILE_NAME);
		//String fileName = company.get().getId() + "_" + SUFFIX_COMPANY_IMAGE_FILE_NAME;

		Map response = null;

		try {
			// push to storage cloud
			response = storageService.uploadFile(fileName, file, FileType.IMAGE,
												 Constant.FileLocation.STORAGE_COMPANY_IMAGE_LOCATION);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (response == null) {
			throw new FileStorageException("Upload image failure");
		}
		if (response.get("secure_url") == null) {
			throw new FileStorageException("Can't get url from response of storage cloud");
		}

		String url = response.get("secure_url").toString();
		company.setImage(url);
		companyRepository.save(company);
		return new DataResponse<>(companyMapper.companyToCompanyDto(company));
	}

	@GetMapping("/application/score-cv")
	public DataResponse<HirizeResponse<AIMatcherData>> scoreCV(@RequestParam Long jobPostId,
			@RequestParam Long candidateId) {
		try {
			HirizeResponse<AIMatcherData> result = employerService.getPointOfApplicationFromHirize(jobPostId,
																								   candidateId);
			return new DataResponse<>(result);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (APIError | InvalidBehaviorException e) {
			throw new RuntimeException(e);
		}
	}

	@GetMapping("/application/hirize-iq-suggest")
	public DataResponse<HirizeResponse<HirizeIQData>> getSuggestionFromHirizeIQ(@RequestParam Long jobPostId,
			@RequestParam Long candidateId) {
		try {
			HirizeResponse<HirizeIQData> result = employerService.getAISuggestForApplicationFromHirize(jobPostId,
																									   candidateId);
			return new DataResponse<>(result);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (APIError | InvalidBehaviorException e) {
			throw new RuntimeException(e);
		}
	}

	@GetMapping("/application/ai-matcher/clear")
	public DataResponse<String> clearAIMatcherData(@RequestParam Long jobPostId, @RequestParam Long candidateId) {
		employerService.clearAIMatcherDataForApplication(jobPostId, candidateId);
		return new DataResponse<>("Success");
	}

	@GetMapping("/application/hirize-iq/clear")
	public DataResponse<String> clearHirizeIQData(@RequestParam Long jobPostId, @RequestParam Long candidateId) {
		employerService.clearHirizeIQDataForApplication(jobPostId, candidateId);
		return new DataResponse<>("Success");
	}

	@GetMapping("/application/ai-matcher/check-existed")
	public DataResponse<Boolean> checkAIMatcherDataExisted(@RequestParam Long jobPostId, @RequestParam Long candidateId) {
		boolean isExisted = employerService.checkAIMatcherExisted(jobPostId, candidateId);
		return new DataResponse<>(isExisted);
	}

	@GetMapping("/application/hirize-iq/check-existed")
	public DataResponse<Boolean> checkHirizeIQDataExisted(@RequestParam Long jobPostId, @RequestParam Long candidateId) {
		boolean isExisted = employerService.checkHirizeIQExisted(jobPostId, candidateId);
		return new DataResponse<>(isExisted);
	}
}
