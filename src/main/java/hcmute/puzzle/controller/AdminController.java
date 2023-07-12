package hcmute.puzzle.controller;

import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.dtos.news.CreateUserForAdminDto;
import hcmute.puzzle.infrastructure.dtos.news.UpdateUserForAdminDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CategoryDto;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.dtos.olds.InvoiceDto;
import hcmute.puzzle.infrastructure.dtos.request.*;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.mappers.CompanyMapper;
import hcmute.puzzle.infrastructure.models.CompanyFilter;
import hcmute.puzzle.infrastructure.models.DataStaticJoinAccount;
import hcmute.puzzle.infrastructure.repository.EmployerRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.services.impl.AdminService;
import hcmute.puzzle.services.impl.ApplicationService;
import hcmute.puzzle.services.impl.BlogPostService;
import hcmute.puzzle.services.impl.UserService;
import hcmute.puzzle.utils.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = "/admin")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class AdminController {

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

//	@Autowired
//	Converter converter;

	@Autowired
	CompanyService companyService;

	@Autowired
	ExtraInfoService extraInfoService;

	@Autowired
	UserService userService;

	@Autowired
	ApplicationService applicationService;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	EmployerRepository employerRepository;

	@Autowired
	CategoryService categoryService;

	@Autowired
	BlogPostService blogPostService;

	@Autowired
	CompanyMapper companyMapper;

	@Autowired
	AdminService adminService;

	// =========== Company ========================
	@PostMapping("/company")
	public DataResponse<CompanyResponse> createCompany(@ModelAttribute CreateCompanyAdminRequest companyPayload) throws
			NotFoundException {
		CompanyDto companyDTO = companyMapper.createCompanyAdminDtoToCompanyDto(companyPayload);
		//    companyDTO.setName(companyPayload.getName());
		//    companyDTO.setDescription(companyPayload.getDescription());
		//    companyDTO.setWebsite(companyPayload.getWebsite());
		//    companyDTO.setIsActive(companyPayload.isActive());
		CompanyResponse companyResponse = companyService.save(companyDTO);
		return new DataResponse<>(companyResponse);
	}

	@PutMapping("/company/{companyId}")
	public DataResponse<CompanyResponse> updateCompany(@PathVariable(value = "companyId") long companyId,
			@ModelAttribute CreateCompanyAdminRequest companyPayload) throws NotFoundException {
		CompanyDto companyDTO = new CompanyDto();
		companyDTO.setName(companyPayload.getName());
		companyDTO.setDescription(companyPayload.getDescription());
		companyDTO.setWebsite(companyPayload.getWebsite());
		companyDTO.setIsActive(companyPayload.getIsActive());
//		Employer employer = null;
//		if (companyPayload.getCreatedEmployerId() != null) {
//			employer = employerRepository.findById(companyPayload.getCreatedEmployerId())
//										 .orElseThrow(() -> new NotFoundException("NOT_FOUND_EMPLOYER"));
//		}
		CompanyResponse companyResponse = companyService.updateForAdmin(companyId, companyPayload);
		return new DataResponse<>(companyResponse);
	}

	@DeleteMapping("/company/{companyId}")
	public DataResponse<String> deleteCompany(@PathVariable Long companyId) {
		companyService.delete(companyId);
		return new DataResponse<>("Success");
	}

	@GetMapping("/company")
	public DataResponse<Page<CompanyResponse>> getAllCompany(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = this.getPageable(page, size);
		Page<CompanyResponse> companyResponses = companyService.getAll(pageable);
		return new DataResponse<>(companyResponses);
	}

	@GetMapping("/company/active")
	public DataResponse<Page<CompanyResponse>> getAllCompanyInactive(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = this.getPageable(page, size);
		CompanyFilter companyFilter = CompanyFilter.builder().isActive(true).build();
		Page<CompanyResponse> companyResponses = companyService.filterCompany(companyFilter, pageable);
		return new DataResponse<>(companyResponses);
	}

	@GetMapping("/company/{id}")
	public DataResponse<CompanyResponse> getOneCompany(@PathVariable Long id) {
		CompanyResponse companyResponse = companyService.getOneById(id);
		return new DataResponse<>(companyResponse);
	}

	@GetMapping("/company/config-rights/add")
	public DataResponse<String> addConfigRightsEmployerWithCompany(
			@RequestParam Long employerId, @RequestParam Long companyId) {
		adminService.addRightsOfEmployerWithCompany(employerId,
													companyId);
		return new DataResponse<>("Success");
	}

	@GetMapping("/company/config-rights/remove")
	public DataResponse<String> removeConfigRightsEmployerWithCompany(
			@RequestParam Long employerId, @RequestParam Long companyId) {
		adminService.removeRightsOfEmployerWithCompany(employerId,
													   companyId);
		return new DataResponse<>("Success");
	}

	// =========== Account ========================
	@PostMapping("/account")
	public DataResponse<String> saveAccount(@RequestBody @Valid CreateUserForAdminDto user) {
		userService.registerUserForAdmin(user, true);
		return new DataResponse<>("Add user " + user.getEmail() + " success.");
	}

	@DeleteMapping("/account/{id}")
	public DataResponse<String> deleteAccount(@PathVariable Long id) {
		userService.delete(id);
		return new DataResponse<>("Success");
	}

	@GetMapping("/account")
	public DataResponse<Page<UserPostDto>> getAllAccount(Pageable pageable) {
		Page<UserPostDto> userPostDtos = userService.getAll(pageable);
		return new DataResponse<>(userPostDtos);
	}

	@GetMapping("/account/{id}")
	public DataResponse<UserPostDto> getAllAccountById(@PathVariable(value = "id") long id) {
		UserPostDto userPostDto = userService.getOne(id);
		return new DataResponse<>(userPostDto);
	}

	// Company
	@PostMapping("/extra-info")
	public DataResponse<ExtraInfoDto> createExtraInfo(@RequestBody ExtraInfoDto extraInfoDTO) {
		ExtraInfoDto extraInfoDto = extraInfoService.save(extraInfoDTO);
		return new DataResponse<>(extraInfoDto);
	}

	@PutMapping("/extra-info/{extraInfoId}")
	public DataResponse<ExtraInfoDto> updateExtraInfo(@RequestBody ExtraInfoDto extraInfoDTO,
			@PathVariable long extraInfoId) {
		ExtraInfoDto extraInfoDto = extraInfoService.update(extraInfoDTO, extraInfoId);
		return new DataResponse<>(extraInfoDto);
	}

	@DeleteMapping("/extra-info/{id}")
	public DataResponse<String> deleteExtraInfo(@PathVariable Long id) {
		extraInfoService.delete(id);
		return new DataResponse<>("Success");
	}

	@GetMapping("/extra-info")
	public DataResponse<Page<ExtraInfoDto>> getAllExtraInfo(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = getPageable(page, size);
		Page<ExtraInfoDto> extraInfoDtos = extraInfoService.getAll(pageable);
		return new DataResponse<>(extraInfoDtos);
	}

	@GetMapping("/job-post/update-status/{jobPostId}")
	public DataResponse<JobPostDto> getOneExtraInfo(@PathVariable(value = "jobPostId") Long id,
			@RequestParam boolean active) {
		JobPostAdminPostRequest jobPostAdminPostRequest = JobPostAdminPostRequest.builder().isActive(active).build();
		JobPostDto jobPostDto = jobPostService.updateJobPostWithRoleAdmin(id, jobPostAdminPostRequest);
		return new DataResponse<>(jobPostDto);
	}

	private Pageable getPageable(Integer page, Integer size) {
		Pageable pageable = Pageable.unpaged();
		if (page != null && size != null) {
			pageable = Pageable.ofSize(size).withPage(page);
		}
		return pageable;
	}

	@GetMapping("/job-post")
	public DataResponse<Page<JobPostDto>> getJobPostByPage(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		Pageable pageable = this.getPageable(page, size);
		Page<JobPostDto> jobPostDtos = jobPostService.getAll(pageable);
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/get-all-job-post")
	public DataResponse<Page<JobPostDto>> getAllJobPost() {
		Page<JobPostDto> jobPostDtos = jobPostService.getAll(Pageable.unpaged());
		return new DataResponse<>(jobPostDtos);
	}

	@GetMapping("/account/get-amount")
	public DataResponse getAccountAmount() {
		long amount = userService.getAccountAmount();
		return new DataResponse(amount);
	}

	@GetMapping("/job-post/get-amount")
	public DataResponse<Long> getJobPostAmount() {
		long amount = jobPostService.getJobPostAmount();
		return new DataResponse<>(amount);
	}

	@GetMapping("/admin/application/get-amount")
	public DataResponse<Long> getApplicationAmount() {
		Long applicationAmount = applicationService.getApplicationAmount();
		return new DataResponse<>(applicationAmount);
	}

	@GetMapping("/extra-info/{id}")
	public DataResponse<ExtraInfoDto> getExtraInfoById(@PathVariable(value = "id") long id) {
		ExtraInfoDto extraInfoDto = extraInfoService.getOneById(id);
		return new DataResponse<>(extraInfoDto);
	}

	@GetMapping("/statistics/new-account/get-amount/{numWeek}")
	public DataResponse<List<DataStaticJoinAccount>> getDataJoinAccountByNumWeek(
			@PathVariable(value = "numWeek") long numWeek) {
		List<DataStaticJoinAccount> dataStaticJoinAccounts = userService.getListDataUserJoinLastNumWeeks(numWeek);
		return new DataResponse<>(dataStaticJoinAccounts);
	}

	@Transactional
	@PutMapping("/account/{userId}")
	public DataResponse<UserPostDto> updateAccountById(@PathVariable(value = "userId") long userId,
			@ModelAttribute UpdateUserForAdminDto updateUserForAdminDto, @RequestPart(required = false) MultipartFile avatar) {
		if (avatar != null ) {
			updateUserForAdminDto.setAvatarFile(avatar);
		}
		UserPostDto userPostDto = userService.updateUserForAdmin(userId, updateUserForAdminDto);
		return new DataResponse<>(userPostDto);
	}

	@GetMapping("/invoice")
	public DataResponse<List<InvoiceDto>> getAllInvoice(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(required = false) Integer size) {
//		Pageable pageable = this.getPageable(page, size);
//		pageable.getSort().and(Sort.by("createdAt").descending());
		List<InvoiceDto> invoiceDtos = invoiceService.getAllInvoice();
		return new DataResponse<>(invoiceDtos);
	}

	@PostMapping("/invoice/get-by-time-frame")
	public DataResponse<List<InvoiceDto>> getAllInvoiceByTimeFrame(@RequestBody TimeFramePayLoad timeFrame) {
		List<InvoiceDto> invoiceDtos = invoiceService.getAllInvoiceByTimeFrame(timeFrame.getStartTime(),
																			   timeFrame.getEndTime());
		return new DataResponse<>(invoiceDtos);
	}

	@GetMapping("/invoice/{invoiceId}")
	public DataResponse<InvoiceDto> getOneInvoice(@PathVariable(value = "invoiceId") long invoiceId) {
		InvoiceDto invoiceDto = invoiceService.getOneInvoice(invoiceId);
		return new DataResponse<>(invoiceDto);
	}

	@GetMapping("/invoice/total-revenue")
	public DataResponse<Long> getTotalRevenue() {
		long totalRevenue = invoiceService.getTotalRevenue();
		return new DataResponse<>(totalRevenue);
	}

	@PostMapping("/invoice/total-revenue/get-by-time-frame")
	public DataResponse<Long> getTotalRevenueByTimeFrame(@RequestBody TimeFramePayLoad timeFrame) {
		long totalRevenue = invoiceService.getTotalRevenue(timeFrame.getStartTime(), timeFrame.getEndTime());
		return new DataResponse<>(totalRevenue);
	}

	@GetMapping("/category")
	public DataResponse getAllCategory() {
		return categoryService.getAll();
	}

	@RequestMapping(
			path = "/category",
			method = RequestMethod.POST,
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public DataResponse<CategoryDto> addCategory(@ParameterObject @ModelAttribute CreateCategoryRequest createCategoryRequest,
			@RequestPart(required = false) MultipartFile image) {
		if (createCategoryRequest.getImageFile() == null && image != null) {
			createCategoryRequest.setImageFile(image);
		}
		CategoryDto categoryDto = categoryService.save(createCategoryRequest);
		return new DataResponse<>(categoryDto);
	}

	@RequestMapping(
			path = "/category/{categoryId}",
			method = RequestMethod.PUT,
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public DataResponse<CategoryDto> updateCategory(@PathVariable long categoryId,
			@ParameterObject @ModelAttribute CreateCategoryRequest createCategoryRequest,
			@RequestPart(required = false) MultipartFile image) {
		if (createCategoryRequest.getImageFile() == null && image != null) {
			createCategoryRequest.setImageFile(image);
		}
		CategoryDto categoryDto = categoryService.update(createCategoryRequest, categoryId);
		return new DataResponse<>(categoryDto);
	}

	@DeleteMapping("/category/{categoryId}")
	public DataResponse deleteCategory(@PathVariable long categoryId) {
		return categoryService.delete(categoryId);
	}

	@DeleteMapping("/blog-post/{blogPostId}")
	public DataResponse deleteBlogPost(@PathVariable long blogPostId) {
		return blogPostService.delete(blogPostId);
	}
}
