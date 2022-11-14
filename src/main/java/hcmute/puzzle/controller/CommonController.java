package hcmute.puzzle.controller;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobAlertEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.model.CandidateFilter;
import hcmute.puzzle.model.JobPostFilter;
import hcmute.puzzle.model.SearchBetween;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class CommonController {

  @Autowired CandidateService candidateService;

  @Autowired UserRepository userRepository;

  @Autowired JobPostService jobPostService;

  @Autowired CandidateRepository candidateRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired ApplicationRepository applicationRepository;

  @Autowired
  Converter converter;

  @Autowired
  ExtraInfoService extraInfoService;

  @Autowired CompanyService companyService;

  @Autowired
  SearchService searchService;

  @GetMapping("/common/job-post/get-all")
  ResponseObject getAllJobPost() {
    return jobPostService.getAll();
  }

  @GetMapping("/common/job-post/get-one/{jobPostId}")
  ResponseObject getJobPostById(@PathVariable(value = "jobPostId") long jobPostId) {
    return jobPostService.getOne(jobPostId);
  }

  @GetMapping("/common/company")
  public ResponseObject getAllCompany() {
    return companyService.getAll();
  }

  @GetMapping("/common/company/get-one-company/{id}")
  public ResponseObject getOneCompany(@PathVariable Long id) {
    return companyService.getOneById(id);
  }


  @GetMapping("/common/get-all-extra-info-by-type")
  public ResponseObject getAllExtraInfoByType(@RequestParam String type) {
    return extraInfoService.getByType(type);
  }

  @PostMapping("/common/job-post-filter")
  public ResponseObject filterJobPost(@RequestBody(required = false) JobPostFilter jobPostFilter) {

    List<SearchBetween> searchBetweenList = new ArrayList<>();

    SearchBetween searchForBudgetMin = new SearchBetween("minBudget", Double.valueOf(jobPostFilter.getMinBudget()), null);
    SearchBetween searchForExperienceYearMin = new SearchBetween("experienceYear", null, Double.valueOf(jobPostFilter.getExperienceYear()));

    searchBetweenList.add(searchForBudgetMin);
    searchBetweenList.add(searchForExperienceYearMin);

    Map<String,List<String>> fieldSearch = new HashMap<>();
    if (jobPostFilter.getTitles() != null && !jobPostFilter.getTitles().isEmpty()) {
      fieldSearch.put("title", jobPostFilter.getTitles());
    }

    if (jobPostFilter.getEmploymentTypes() != null && !jobPostFilter.getEmploymentTypes().isEmpty()) {
      fieldSearch.put("employmentType", jobPostFilter.getEmploymentTypes());
    }

    if (jobPostFilter.getCities() != null && !jobPostFilter.getCities().isEmpty()) {
      fieldSearch.put("city", jobPostFilter.getCities());
    }

    if (jobPostFilter.getPositions() != null && !jobPostFilter.getPositions().isEmpty()) {
      fieldSearch.put("positions", jobPostFilter.getPositions());
    }

    if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
      fieldSearch.put("skills", jobPostFilter.getSkills());
    }

    List<String> commonFieldSearch = new ArrayList<>();
    List<String> valueCommonFieldSearch = jobPostFilter.getOthers();

    if (jobPostFilter.getOthers() != null && !jobPostFilter.getOthers().isEmpty()) {
      commonFieldSearch.add("description");
      commonFieldSearch.add("title");
    }

    List<JobPostEntity> jobPostEntities = searchService.filterObject(
            "JobPostEntity",
            searchBetweenList,
            fieldSearch,
            commonFieldSearch,
            valueCommonFieldSearch,
            jobPostFilter.getNoOfRecords(),
            jobPostFilter.getPageIndex(),
            jobPostFilter.isSortById());

    List<JobPostDTO> jobPostDTOS = jobPostEntities.stream().map(jobPost -> converter.toDTO(jobPost)).collect(Collectors.toList());

    //JobPostFilter jobPostFilter1 = new JobPostFilter();

    return new ResponseObject(200 , "Result for filter job post", jobPostDTOS);
  }

  @PostMapping("/common/candidate-filter")
  public ResponseObject filterCandidate(@RequestBody(required = false) CandidateFilter candidateFilter) {

    Map<String,List<String>> fieldSearch = new HashMap<>();
    if (candidateFilter.getEducationLevels() != null && !candidateFilter.getEducationLevels().isEmpty()) {
      fieldSearch.put("educationLevel", candidateFilter.getEducationLevels());
    }

    if (candidateFilter.getSkills() != null && !candidateFilter.getSkills().isEmpty()) {
      fieldSearch.put("skills", candidateFilter.getSkills());
    }

    if (candidateFilter.getPositions() != null && !candidateFilter.getPositions().isEmpty()) {
      fieldSearch.put("positions", candidateFilter.getPositions());
    }

    if (candidateFilter.getServices() != null && !candidateFilter.getServices().isEmpty()) {
      fieldSearch.put("services", candidateFilter.getServices());
    }

    List<String> commonFieldSearch = new ArrayList<>();
    List<String> valueCommonFieldSearch = candidateFilter.getOthers();

    if (candidateFilter.getOthers() != null && !candidateFilter.getOthers().isEmpty()) {
      commonFieldSearch.add("introduction");
      commonFieldSearch.add("phoneNum");
    }

    List<CandidateEntity> candidateEntity = searchService.filterObject(
            "CandidateEntity",
            null,
            fieldSearch,
            commonFieldSearch,
            valueCommonFieldSearch,
            candidateFilter.getNoOfRecords(),
            candidateFilter.getPageIndex(),
            candidateFilter.isSortById());

    List<CandidateDTO> candidateDTOS = candidateEntity.stream().map(candidate -> converter.toDTO(candidate)).collect(Collectors.toList());

    //CandidateFilter candidateFilter1 = new CandidateFilter();

    return new ResponseObject(200 , "Result for filter candidate", candidateDTOS);
  }

  @GetMapping("/common/get-all-extra-info")
  public ResponseObject getAllExtraInfo() {
    return extraInfoService.getAll();
  }

}
