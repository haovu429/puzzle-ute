package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ExtraInfoDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
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

  // Company, add new company
  @PostMapping("/admin/add-company")
  public ResponseObject createCompany(@RequestBody CompanyDTO companyDTO) {
    return companyService.save(companyDTO);
  }

  @PutMapping("/admin/update-info-company")
  public ResponseObject updateCompany(@RequestBody CompanyDTO companyDTO) {
    return companyService.update(companyDTO);
  }

  @GetMapping("/admin/delete-info-company/{id}")
  public ResponseObject deleteCompany(@PathVariable Long id) {
    return companyService.delete(id);
  }

  @GetMapping("/admin/get-all-company")
  public ResponseObject getAllCompany() {
    return companyService.getAll();
  }

  @GetMapping("/admin/get-all-company-inactive")
  public ResponseObject getAllCompanyInactive() {
    return companyService.getAllCompanyInActive();
  }

  @GetMapping("/admin/get-one-company/{id}")
  public ResponseObject getOneCompany(@PathVariable Long id) {
    return companyService.getOneById(id);
  }

  // Account
  @PostMapping("/admin/add-account")
  public ResponseObject saveAccount(@RequestBody UserDTO user) {
    return userService.save(user);
  }

  @DeleteMapping("/admin/delete-account/{id}")
  public ResponseObject deleteAccount(@PathVariable Long id) {
    return userService.delete(id);
  }

  @PutMapping("/admin/update-account/{id}")
  public ResponseObject updateAccount(@PathVariable Long id, @RequestBody UserDTO user) {
    return userService.update(id, user);
  }

  @GetMapping("/admin/get-all-account")
  public ResponseObject getAllAccount() {
    return userService.getAll();
  }

  @GetMapping("/admin/get-account-by-id/{id}")
  public ResponseObject getAllAccountById(@PathVariable(value = "id") long id) {
    return userService.getOne(id);
  }

  // Company
  @PostMapping("/admin/add-extra-info")
  public ResponseObject createExtraInfo(@RequestBody ExtraInfoDTO extraInfoDTO) {
    return extraInfoService.save(extraInfoDTO);
  }

  @PutMapping("/admin/update-extra-info")
  public ResponseObject updateExtraInfo(@RequestBody ExtraInfoDTO extraInfoDTO) {
    return extraInfoService.update(extraInfoDTO);
  }

  @GetMapping("/admin/delete-extra-info/{id}")
  public ResponseObject deleteExtraInfo(@PathVariable Long id) {
    return extraInfoService.delete(id);
  }

  @GetMapping("/admin/get-all-extra-info")
  public ResponseObject getAllExtraInfo() {
    return extraInfoService.getAll();
  }


  @GetMapping("/admin/update-status-job-post/{jobPostId}")
  public ResponseObject getOneExtraInfo(
      @PathVariable(value = "jobPostId") Long id, @RequestParam boolean active) {

    if (active) {
      return jobPostService.activateJobPost(id);
    }
    return jobPostService.deactivateJobPost(id);
  }

  @GetMapping("/admin/get-all-job-by-page")
  public ResponseObject getJobPostByPage(
          @RequestParam(value = "page", required = false) Integer page, @RequestParam(required = false) Integer numOfRecord) {
    if (page == null) {
      page = 0;
    }

    if (numOfRecord == null) {
      numOfRecord = 10;
    }
    return jobPostService.getJobPostWithPage(page, numOfRecord);
  }

  @GetMapping("/admin/get-all-job-post")
  public ResponseObject getAllJobPost() {
    return jobPostService.getAll();
  }

  @PutMapping("/admin/company/{id}")
  public ResponseObject updateCompany(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
    companyDTO.setId(id);
    return companyService.update(companyDTO);
    //return null;
  }

  @GetMapping("/admin/get-account-amount")
  public ResponseObject getAccountAmount() {
    return userService.getAccountAmount();
  }

  @GetMapping("/admin/get-job-post-amount")
  public ResponseObject getJobPostAmount() {
    return jobPostService.getJobPostAmount();
  }

  @GetMapping("/admin/get-application-amount")
  public ResponseObject getApplicationAmount() {
    return applicationService.getApplicationAmount();
  }

  @GetMapping("/admin/get-extra-info/{id}")
  public ResponseObject getExtraInfoById(@PathVariable(value = "id")long id) {
    return extraInfoService.getOneById(id);
  }

  @GetMapping("/admin/get-data-join-account-in-last-num-week/{numWeek}")
  public ResponseObject getDataJoinAccountByNumWeek(@PathVariable(value = "numWeek")long numWeek) {
    return userService.getListDataUserJoinLastNumWeeks(numWeek);
  }

}
