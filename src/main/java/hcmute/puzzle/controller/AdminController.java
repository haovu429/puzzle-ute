package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ExtraInfoDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.model.payload.request.company.CreateCompanyPayload;
import hcmute.puzzle.model.payload.request.company.CreateCompanyPayloadForAdmin;
import hcmute.puzzle.model.payload.request.other.TimeFramePayLoad;
import hcmute.puzzle.model.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/admin")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class AdminController {

  @Autowired EmployerService employerService;

  @Autowired UserRepository userRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired JobPostService jobPostService;

  @Autowired Converter converter;

  @Autowired CompanyService companyService;

  @Autowired ExtraInfoService extraInfoService;

  @Autowired UserService userService;

  @Autowired ApplicationService applicationService;

  @Autowired InvoiceService invoiceService;

  // Company, add new company
  @PostMapping("/create-info-company")
  public ResponseObject createCompany(
          @ModelAttribute CreateCompanyPayloadForAdmin companyPayload, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    CompanyDTO companyDTO = new CompanyDTO();
    companyDTO.setName(companyPayload.getName());
    companyDTO.setDescription(companyPayload.getDescription());
    companyDTO.setWebsite(companyPayload.getWebsite());
    companyDTO.setActive(companyPayload.isActive());
    return companyService.save(companyDTO,companyPayload.getImage(), userDetails.getUser().getEmployerEntity());
  }

  @PutMapping("/update-info-company")
  public ResponseObject updateCompany(@RequestBody CompanyDTO companyDTO) {
    return companyService.update(companyDTO);
  }

  @GetMapping("/delete-info-company/{id}")
  public ResponseObject deleteCompany(@PathVariable Long id) {
    return companyService.delete(id);
  }

  @GetMapping("/get-all-company")
  public ResponseObject getAllCompany() {
    return companyService.getAll();
  }

  @GetMapping("/get-all-company-inactive")
  public ResponseObject getAllCompanyInactive() {
    return companyService.getAllCompanyInActive();
  }

  @GetMapping("/get-one-company/{id}")
  public ResponseObject getOneCompany(@PathVariable Long id) {
    return companyService.getOneById(id);
  }

  // Account
  @PostMapping("/add-account")
  public ResponseObject saveAccount(@RequestBody UserDTO user) {
    return userService.save(user);
  }

  @DeleteMapping("/delete-account/{id}")
  public ResponseObject deleteAccount(@PathVariable Long id) {
    return userService.delete(id);
  }

  //  @PutMapping("/update-account/{id}")
  //  public ResponseObject updateAccount(@PathVariable Long id, @RequestBody UserDTO user) {
  //    return userService.update(id, user);
  //  }

  @GetMapping("/get-all-account")
  public ResponseObject getAllAccount() {
    return userService.getAll();
  }

  @GetMapping("/get-account-by-id/{id}")
  public ResponseObject getAllAccountById(@PathVariable(value = "id") long id) {
    return userService.getOne(id);
  }

  // Company
  @PostMapping("/add-extra-info")
  public ResponseObject createExtraInfo(@RequestBody ExtraInfoDTO extraInfoDTO) {
    return extraInfoService.save(extraInfoDTO);
  }

  @PutMapping("/update-extra-info")
  public ResponseObject updateExtraInfo(@RequestBody ExtraInfoDTO extraInfoDTO) {
    return extraInfoService.update(extraInfoDTO);
  }

  @GetMapping("/delete-extra-info/{id}")
  public ResponseObject deleteExtraInfo(@PathVariable Long id) {
    return extraInfoService.delete(id);
  }

  @GetMapping("/get-all-extra-info")
  public ResponseObject getAllExtraInfo() {
    return extraInfoService.getAll();
  }

  @GetMapping("/update-status-job-post/{jobPostId}")
  public ResponseObject getOneExtraInfo(
      @PathVariable(value = "jobPostId") Long id, @RequestParam boolean active) {

    if (active) {
      return jobPostService.activateJobPost(id);
    }
    return jobPostService.deactivateJobPost(id);
  }

  @GetMapping("/get-all-job-by-page")
  public ResponseObject getJobPostByPage(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(required = false) Integer numOfRecord) {
    if (page == null) {
      page = 0;
    }

    if (numOfRecord == null) {
      numOfRecord = 10;
    }
    return jobPostService.getJobPostWithPage(page, numOfRecord);
  }

  @GetMapping("/get-all-job-post")
  public ResponseObject getAllJobPost() {
    return jobPostService.getAll();
  }

  @PutMapping("/company/{id}")
  public ResponseObject updateCompany(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
    companyDTO.setId(id);
    return companyService.update(companyDTO);
    // return null;
  }

  @GetMapping("/get-account-amount")
  public ResponseObject getAccountAmount() {
    return userService.getAccountAmount();
  }

  @GetMapping("/get-job-post-amount")
  public ResponseObject getJobPostAmount() {
    return jobPostService.getJobPostAmount();
  }

  @GetMapping("/get-application-amount")
  public ResponseObject getApplicationAmount() {
    return applicationService.getApplicationAmount();
  }

  @GetMapping("/get-extra-info/{id}")
  public ResponseObject getExtraInfoById(@PathVariable(value = "id") long id) {
    return extraInfoService.getOneById(id);
  }

  @GetMapping("/get-data-join-account-in-last-num-week/{numWeek}")
  public ResponseObject getDataJoinAccountByNumWeek(@PathVariable(value = "numWeek") long numWeek) {
    return userService.getListDataUserJoinLastNumWeeks(numWeek);
  }

  @PutMapping("/update-account-by-id/{userId}")
  public DataResponse updateAccountById(
      @PathVariable(value = "userId") long userId, @RequestBody UserDTO user) {

    return userService.updateForAdmin(userId, user);
  }

  @GetMapping("/get-all-invoice")
  public DataResponse getAllInvoice() {
    return invoiceService.getAllInvoice();
  }

  @GetMapping("/get-all-invoice-by-time-frame")
  public DataResponse getAllInvoiceByTimeFrame(@RequestBody TimeFramePayLoad timeFrame) {
    return invoiceService.getAllInvoiceByTimeFrame(timeFrame.getStartTime(), timeFrame.getEndTime());
  }

  @GetMapping("/get-one-invoice/{invoiceId}")
  public DataResponse getOneInvoice(@PathVariable(value = "invoiceId") long invoiceId) {
    InvoiceEntity invoice = invoiceService.getOneInvoice(invoiceId);
    return new DataResponse(converter.toDTO(invoice));
  }

  @GetMapping("/get-total-revenue")
  public DataResponse getTotalRevenue() {
    long totalRevenue = invoiceService.getTotalRevenue();
    return new DataResponse(totalRevenue);
  }

  @PostMapping("/get-total-revenue-by-time-frame")
  public DataResponse getTotalRevenueByTimeFrame(@RequestBody TimeFramePayLoad timeFrame) {
    long totalRevenue = invoiceService.getTotalRevenue(timeFrame.getStartTime(), timeFrame.getEndTime());
    return new DataResponse(totalRevenue);
  }
}
