package hcmute.puzzle.controller;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.JobAlertEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.model.JobPostFilter;
import hcmute.puzzle.model.SearchBetween;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.CandidateService;
import hcmute.puzzle.services.ExtraInfoService;
import hcmute.puzzle.services.JobPostService;
import hcmute.puzzle.services.SearchService;
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

  @Autowired
  SearchService searchService;

  @GetMapping("/job-post/get-all")
  ResponseObject getAllJobPost() {
    return jobPostService.getAll();
  }

  @GetMapping("/job-post/get-one/{jobPostId}")
  ResponseObject getJobPostById(@PathVariable(value = "jobPostId") long jobPostId) {
    return jobPostService.getOne(jobPostId);
  }

  @GetMapping("/get-all-extra-info-by-type")
  public ResponseObject getAllExtraInfoByType(@RequestParam String type) {
    return extraInfoService.getByType(type);
  }

  @PostMapping("/job-post/filter")
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

    if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
      commonFieldSearch.add("description");
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
}
