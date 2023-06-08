package hcmute.puzzle.controller;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.olds.JobPostDtoOld;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Application;
import hcmute.puzzle.infrastructure.entities.Company;
import hcmute.puzzle.infrastructure.entities.JobPost;
import hcmute.puzzle.infrastructure.mappers.CompanyMapper;
import hcmute.puzzle.infrastructure.models.ResponseApplication;
import hcmute.puzzle.infrastructure.models.payload.request.company.CreateCompanyRoleUserRequest;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.CompanyRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.mail.MailObject;
import hcmute.puzzle.utils.mail.SendMail;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

	// Delete by user Entity
	//  @DeleteMapping("/deactivate")
	//  ResponseObject deleteEmployer(Authentication authentication) {
	//    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	//    return employerService.delete(userDetails.getUser().getId());
	//  }

	@PutMapping("/update")
	ResponseObject updateEmployer(@RequestBody @Validated EmployerDto employer, BindingResult bindingResult,
			Authentication authentication) {
		if (bindingResult.hasErrors()) {
			throw new CustomException(bindingResult.getFieldError().toString());
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		employer.setUserId(userDetails.getUser().getId());
		employer.setId(userDetails.getUser().getId());

		return employerService.update(employer);
	}

	@GetMapping("/profile")
	ResponseObject getById(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return employerService.getOne(userDetails.getUser().getId());
	}

	@PostMapping("/post-job")
	ResponseObject createJobPost(@RequestBody @Validated JobPostDtoOld jobPostDTO, Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		// Validate JobPost
		jobPostService.validateJobPost(jobPostDTO);
		jobPostService.checkCreatedJobPostLimit(userDetails.getUser().getId());

		// Set default createEmployer is Employer create first (this valid user requesting)
		jobPostDTO.setCreatedEmployerId(userDetails.getUser().getId());

		return new ResponseObject(HttpStatus.OK.value(), "Post job success.", jobPostService.add(jobPostDTO));
	}

	@DeleteMapping("/delete-job-post/{id}")
	DataResponse deleteJobPost(@PathVariable Long id, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		// employer can not change active status
		Optional<JobPost> jobPost = jobPostRepository.findById(id);
		if (jobPost.isEmpty()) {
			throw new CustomException("Job post isn't exists");
		}

		if (jobPost.get().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this jobPost");
		}
		return jobPostService.markJobPostWasDelete(id);
	}

	@PutMapping("/update-job-post")
	ResponseObject updateJobPost(@RequestBody @Validated JobPostDtoOld jobPostDTO, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		// Validate JobPost
		jobPostService.validateJobPost(jobPostDTO);

		// employer can not change active status
		Optional<JobPost> oldJobPost = jobPostRepository.findById(jobPostDTO.getId());
		if (oldJobPost.isEmpty()) {
			throw new CustomException("Job post isn't exists");
		}
		jobPostDTO.setActive(oldJobPost.get().isActive());

		// Set default createEmployer is Employer create first (this valid user requesting)
		jobPostDTO.setCreatedEmployerId(userDetails.getUser().getId());

		return new ResponseObject(HttpStatus.OK.value(), "Post job success.", jobPostService.update(jobPostDTO));
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
	public DataResponse saveCompany(@ModelAttribute CreateCompanyRoleUserRequest companyPayload) throws
			NotFoundException {
		//    if (companyPayload.getImageFile().getSize() > 5 * 1024 * 1024) {
		//      throw new IllegalArgumentException("File size exceeds the limit 5MB");
		//    }
		CompanyDto companyDto = companyMapper.createCompanyRoleUserRequestToCompanyDto(companyPayload);
		return companyService.save(companyDto);
	}

	// Lấy danh sách ứng viên đã apply vào một jobPost
	@GetMapping("/candidate-apply-jobpost/{jobPostId}")
	ResponseObject getCandidateApplyJobPost(@PathVariable Long jobPostId, Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);

		if (jobPost.isEmpty()) {
			throw new CustomException("JobPost isn't exist");
		}

		if (jobPost.get().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this jobPost");
		}

		return jobPostService.getCandidatesApplyJobPost(jobPostId);
	}

	@GetMapping("/response-application")
	ResponseObject responseApplication(@RequestParam long applicationId, @RequestParam boolean result,
			@RequestParam String note, Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Optional<Application> application = applicationRepository.findById(applicationId);

		if (application.isEmpty()) {
			throw new CustomException("Application isn't exist");
		}

		if (application.get().getJobPost().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this application");
		}

		return applicationService.responseApplication(applicationId, result, note);
	}

	@PostMapping("/response-application-by-candidate-and-job-post")
	ResponseObject responseApplicationByCandidateIdAndJobPostId(@RequestBody ResponseApplication responseApplication,
			Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Optional<Application> application = applicationRepository.findApplicationByCanIdAndJobPostId(
				responseApplication.getCandidateId(), responseApplication.getJobPostId());

		if (application.isEmpty()) {
			throw new CustomException("Application isn't exist");
		}

		if (application.get().getJobPost().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this application");
		}

		application.get().setNote(responseApplication.getNote());

		String strResult = "ACCEPT";
		if (!responseApplication.isResult()) {
			strResult = "REJECT";
		}

		String contentMail = "Result: " + strResult + "\nEmail HR contact: " + responseApplication.getEmail() + "\n" + responseApplication.getNote();

		MailObject mailObject = new MailObject(application.get().getCandidate().getEmailContact(),
											   responseApplication.getSubject(), contentMail, null);

		Runnable myRunnable = new Runnable() {
			public void run() {
				SendMail.sendMail(mailObject);
			}
		};

		Thread thread = new Thread(myRunnable);
		thread.start();

		// return new ResponseObject(200, "Response success", new ResponseApplication());

		return applicationService.responseApplicationByCandidateAndJobPost(responseApplication.getCandidateId(),
																		   responseApplication.getJobPostId(),
																		   responseApplication.isResult(),
																		   responseApplication.getNote());
	}

	@GetMapping("/get-all-job-post-created")
	ResponseObject getJobPostCreated(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return jobPostService.getJobPostCreatedByEmployerId(userDetails.getUser().getId());
	}

	@GetMapping("/get-all-job-post-created-active")
	ResponseObject getJobPostCreatedActive(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return jobPostService.getActiveJobPostByCreateEmployerId(userDetails.getUser().getId());
	}

	@GetMapping("/get-all-job-post-created-inactive")
	ResponseObject getJobPostCreatedInactive(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return jobPostService.getInactiveJobPostByCreateEmployerId(userDetails.getUser().getId());
	}

	@GetMapping("/get-application-by-job-post/{jobPostId}")
	ResponseObject getApplicationByJobPost(@PathVariable long jobPostId) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
		if (jobPost.isEmpty()) {
			throw new CustomException("Job post isn't exists");
		}

		if (jobPost.get().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this job post");
		}

		return applicationService.getApplicationByJobPostId(jobPostId);
	}

	@GetMapping("/get-candidate-and-result-by-job-post/{jobPostId}")
	DataResponse getCandidateAndApplicationResultByJobPost(@PathVariable long jobPostId) {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);
		if (jobPost.isEmpty()) {
			throw new CustomException("Job post isn't exists");
		}

		if (jobPost.get().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this job post");
		}

		return applicationService.getCandidateAppliedToJobPostIdAndResult(jobPostId);
	}

	@GetMapping("/get-all-candidate-and-result-to-employer")
	DataResponse getCandidateAndApplicationResultByEmployerId(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return applicationService.getCandidateAppliedToEmployerAndResult(userDetails.getUser().getId());
	}

	// This API get amount application to one Employer by employer id
	// , from all job post which this employer created
	@GetMapping("/get-amount-application-to-employer")
	DataResponse getAmountApplicationToEmployer(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return applicationService.getAmountApplicationToEmployer(userDetails.getUser().getId());
	}

	@GetMapping("/get-application-rate-of-job-post/{jobPostId}")
	DataResponse getApplicationRateOfJobPost(@PathVariable(value = "jobPostId") long jobPostId) {
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

		return jobPostService.getApplicationRateByJobPostId(jobPostId);
	} // getApplicationRateEmployerId

	@GetMapping("/get-application-rate-of-employer")
	DataResponse getApplicationRateOfEmployer(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return employerService.getApplicationRateEmployerId(userDetails.getUser().getId());
	}

	@GetMapping("/get-total-job-post-view-of-employer")
	DataResponse getTotalJobPostViewOfEmployer(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long total = jobPostService.getTotalJobPostViewOfEmployer(userDetails.getUser().getId());
		return new DataResponse(total);
	}

	@GetMapping("/get-limit-num-of-job-post-created")
	DataResponse getLimitNumOfJobPostCreated(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long limit = jobPostService.getLimitNumberOfJobPostsCreatedForEmployer(userDetails.getUser().getId());
		return new DataResponse(limit);
	}

	@GetMapping("/get-created-company")
	DataResponse getCreatedCompany(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return companyService.getCreatedCompanyByEmployerId(userDetails.getUser().getId());
	}

	@PostMapping("/upload-image-company/{companyId}")
	public ResponseObject<UserPostDto> uploadAvatar(@PathVariable(value = "companyId") long companyId,
			@RequestParam("file") MultipartFile file, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Optional<Company> company = companyRepository.findById(companyId);

		if (company.isEmpty()) {
			throw new CustomException("Company isn't exists");
		}

		if (company.get().getCreatedEmployer().getId() != userDetails.getUser().getId()) {
			throw new CustomException("You don't have rights for this company");
		}
		String fileName = String.format("%s_%s", company.get().getId(), SUFFIX_COMPANY_IMAGE_FILE_NAME);
		//String fileName = company.get().getId() + "_" + SUFFIX_COMPANY_IMAGE_FILE_NAME;

		Map response = null;

		try {
			// push to storage cloud
			response = storageService.uploadFile(fileName, file, Constant.FileLocation.STORAGE_COMPANY_IMAGE_LOCATION);

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

		company.get().setImage(url);

		companyRepository.save(company.get());

		return new ResponseObject(200, "Upload image success", response);
	}
}
