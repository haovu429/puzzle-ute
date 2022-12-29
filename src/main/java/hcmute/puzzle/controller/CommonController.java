package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CandidateDTO;
import hcmute.puzzle.dto.JobPostDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobPostEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.model.CandidateFilter;
import hcmute.puzzle.model.JobPostFilter;
import hcmute.puzzle.model.ModelQuery;
import hcmute.puzzle.model.SearchBetween;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.*;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class CommonController {

  @Autowired CandidateService candidateService;

  @Autowired UserRepository userRepository;

  @Autowired EmployerService employerService;

  @Autowired JobPostService jobPostService;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired ApplicationRepository applicationRepository;

  @Autowired UserService userService;

  @Autowired Converter converter;

  @Autowired ExtraInfoService extraInfoService;

  @Autowired CompanyService companyService;

  @Autowired SearchService searchService;

  @Autowired ApplicationService applicationService;

  @Autowired ExperienceService experienceService;

  @GetMapping("/common/job-post/get-all")
  ResponseObject getAllJobPost() {
    return jobPostService.getAll();
  }

  @GetMapping("/common/job-post/get-one/{jobPostId}")
  ResponseObject getJobPostById(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable(value = "jobPostId") long jobPostId) {
    jobPostService.countJobPostView(jobPostId);
    if (token != null || !token.isEmpty() || !token.isBlank()) {
      Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);
      jobPostService.viewJobPost(linkUser.get().getId(), jobPostId);
    }
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
    Map<String, List<ModelQuery>> fieldSearchValue = new HashMap<>();
    Map<String, List<ModelQuery>> fieldSearchValueSpecial = new HashMap<>();
    List<SearchBetween> searchBetweenList = new ArrayList<>();

    //    if (jobPostFilter.getNumDayAgo() != -1 && jobPostFilter.getNumDayAgo() > 0) {
    //      TimeUtil timeUtil = new TimeUtil();
    //      Date timeLine = timeUtil.upDownTime_TimeUtil(new Date(), jobPostFilter.getNumDayAgo(),
    // 0, 0);
    //      SearchBetween searchForCreateTime =
    //          new SearchBetween(
    //              "createTime", new ModelQuery(
    //                  ModelQuery.TYPE_QUERY_GREATER,
    //                  ModelQuery.TYPE_ATTRIBUTE_DATE,TimeUtil.dateToString(timeLine,
    // TimeUtil.FORMAT_TIME)), null);
    //      searchBetweenList.add(searchForCreateTime);
    //    }

    if (jobPostFilter.getNumDayAgo() != -1 && jobPostFilter.getNumDayAgo() > 0) {
      TimeUtil timeUtil = new TimeUtil();
      Date timeLine = timeUtil.upDownTime_TimeUtil(new Date(), jobPostFilter.getNumDayAgo(), 0, 0);
      List<ModelQuery> idJobCreateTimeGreater =
          jobPostRepository.getJobPostIdByActiveAndLessThenCreatedTime(true, timeLine).stream()
              .map(
                  id ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_IN, ModelQuery.TYPE_ATTRIBUTE_NUMBER, id))
              .collect(Collectors.toList());
      System.out.println(idJobCreateTimeGreater.size());
      fieldSearchValueSpecial.put("id", idJobCreateTimeGreater);
    }

    if (jobPostFilter.getMinBudget() != null) {
      SearchBetween searchForBudgetMin =
          new SearchBetween(
              "minBudget",
              new ModelQuery(
                  ModelQuery.TYPE_QUERY_GREATER,
                  ModelQuery.TYPE_ATTRIBUTE_NUMBER,
                  Double.valueOf(jobPostFilter.getMinBudget())),
              null);
      searchBetweenList.add(searchForBudgetMin);
    }

    if (jobPostFilter.getMaxBudget() != null) {
      SearchBetween searchForBudgetMax =
          new SearchBetween(
              "maxBudget",
              null,
              new ModelQuery(
                  ModelQuery.TYPE_QUERY_LESS,
                  ModelQuery.TYPE_ATTRIBUTE_NUMBER,
                  Double.valueOf(jobPostFilter.getMaxBudget())));
      searchBetweenList.add(searchForBudgetMax);
    }

    if (jobPostFilter.getExperienceYear() != null
        && !jobPostFilter.getExperienceYear().isEmpty()
        && Double.valueOf(jobPostFilter.getExperienceYear().get(0)) > 0) {
      SearchBetween searchForExperienceYearMin =
          new SearchBetween(
              "experienceYear",
              null,
              new ModelQuery(
                  ModelQuery.TYPE_QUERY_LESS,
                  ModelQuery.TYPE_ATTRIBUTE_NUMBER,
                  Double.valueOf(jobPostFilter.getExperienceYear().get(0))));
      searchBetweenList.add(searchForExperienceYearMin);
    }

    if (jobPostFilter.getTitles() != null && !jobPostFilter.getTitles().isEmpty()) {
      fieldSearchValue.put(
          "title",
          jobPostFilter.getTitles().stream()
              .map(
                  title ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, title))
              .collect(Collectors.toList()));
    }

    //    if (jobPostFilter.getExperienceYear() != null &&
    // !jobPostFilter.getExperienceYear().isEmpty()) {
    //      List<Long> expNums =
    //          jobPostFilter.getExperienceYear().stream()
    //              .map(exp -> Long.valueOf(exp))
    //              .collect(Collectors.toList());
    //      fieldSearchValue.put(
    //          "experienceYear",
    //          expNums.stream()
    //              .map(
    //                  num ->
    //                      new ModelQuery(
    //                          ModelQuery.TYPE_QUERY_EQUAL,
    //                          ModelQuery.TYPE_ATTRIBUTE_NUMBER,
    //                          Long.valueOf(num)))
    //              .collect(Collectors.toList()));
    //    }

    if (jobPostFilter.getEmploymentTypes() != null
        && !jobPostFilter.getEmploymentTypes().isEmpty()) {
      fieldSearchValue.put(
          "employmentType",
          jobPostFilter.getEmploymentTypes().stream()
              .map(
                  employmentType ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE,
                          ModelQuery.TYPE_ATTRIBUTE_STRING,
                          employmentType))
              .collect(Collectors.toList()));
    }

    if (jobPostFilter.getCities() != null && !jobPostFilter.getCities().isEmpty()) {
      fieldSearchValue.put(
          "city",
          jobPostFilter.getCities().stream()
              .map(
                  city ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, city))
              .collect(Collectors.toList()));
    }

    if (jobPostFilter.getPositions() != null && !jobPostFilter.getPositions().isEmpty()) {
      fieldSearchValue.put(
          "positions",
          jobPostFilter.getPositions().stream()
              .map(
                  position ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, position))
              .collect(Collectors.toList()));
    }

    if (jobPostFilter.getSkills() != null && !jobPostFilter.getSkills().isEmpty()) {
      fieldSearchValue.put(
          "skills",
          jobPostFilter.getSkills().stream()
              .map(
                  skill ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, skill))
              .collect(Collectors.toList()));
    }

    // filer job post active
    List<Boolean> booleanList = new ArrayList<>();
    booleanList.add(true);

    fieldSearchValue.put(
        "isActive",
        booleanList.stream()
            .map(
                boo ->
                    new ModelQuery(
                        ModelQuery.TYPE_QUERY_EQUAL, ModelQuery.TYPE_ATTRIBUTE_BOOLEAN, boo))
            .collect(Collectors.toList()));

    List<String> commonFieldSearch = new ArrayList<>();
    List<ModelQuery> valueCommonFieldSearch = null;
    if (jobPostFilter.getOthers() != null && !jobPostFilter.getOthers().isEmpty()) {
      valueCommonFieldSearch =
          jobPostFilter.getOthers().stream()
              .map(
                  other ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, other))
              .collect(Collectors.toList());

      commonFieldSearch.add("description");
      commonFieldSearch.add("title");
    }

    List<JobPostEntity> jobPostEntities =
        searchService.filterObject(
            "JobPostEntity",
            searchBetweenList,
            fieldSearchValue,
            fieldSearchValueSpecial,
            commonFieldSearch,
            valueCommonFieldSearch,
            jobPostFilter.getNoOfRecords(),
            jobPostFilter.getPageIndex(),
            jobPostFilter.isSortById());

    List<JobPostDTO> jobPostDTOS =
        jobPostEntities.stream()
            .map(jobPost -> converter.toDTO(jobPost))
            .collect(Collectors.toList());

    // JobPostFilter jobPostFilter1 = new JobPostFilter();

    return new ResponseObject(200, "Result for filter job post", jobPostDTOS);
  }

  @PostMapping("/common/candidate-filter")
  public ResponseObject filterCandidate(@RequestBody CandidateFilter candidateFilter) {

    Map<String, List<ModelQuery>> fieldSearchValue = new HashMap<>();
    if (candidateFilter.getEducationLevels() != null
        && !candidateFilter.getEducationLevels().isEmpty()) {
      fieldSearchValue.put(
          "educationLevel",
          candidateFilter.getEducationLevels().stream()
              .map(
                  educationLevel ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE,
                          ModelQuery.TYPE_ATTRIBUTE_STRING,
                          educationLevel))
              .collect(Collectors.toList()));
    }

    if (candidateFilter.getSkills() != null && !candidateFilter.getSkills().isEmpty()) {
      fieldSearchValue.put(
          "skills",
          candidateFilter.getSkills().stream()
              .map(
                  skill ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, skill))
              .collect(Collectors.toList()));
    }

    if (candidateFilter.getPositions() != null && !candidateFilter.getPositions().isEmpty()) {
      fieldSearchValue.put(
          "positions",
          candidateFilter.getPositions().stream()
              .map(
                  position ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, position))
              .collect(Collectors.toList()));
    }

    if (candidateFilter.getServices() != null && !candidateFilter.getServices().isEmpty()) {
      fieldSearchValue.put(
          "services",
          candidateFilter.getServices().stream()
              .map(
                  service ->
                      new ModelQuery(
                          ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, service))
              .collect(Collectors.toList()));
    }

    List<String> commonFieldSearch = new ArrayList<>();
    List<ModelQuery> valueCommonFieldSearch =
        candidateFilter.getOthers().stream()
            .map(
                other ->
                    new ModelQuery(
                        ModelQuery.TYPE_QUERY_LIKE, ModelQuery.TYPE_ATTRIBUTE_STRING, other))
            .collect(Collectors.toList());

    if (candidateFilter.getOthers() != null && !candidateFilter.getOthers().isEmpty()) {
      commonFieldSearch.add("introduction");
      commonFieldSearch.add("phoneNum");
    }

    List<CandidateEntity> candidateEntity =
        searchService.filterObject(
            "CandidateEntity",
            null,
            fieldSearchValue,
            null,
            commonFieldSearch,
            valueCommonFieldSearch,
            candidateFilter.getNoOfRecords(),
            candidateFilter.getPageIndex(),
            candidateFilter.isSortById());

    List<CandidateDTO> candidateDTOS =
        candidateEntity.stream()
            .map(candidate -> converter.toDTO(candidate))
            .collect(Collectors.toList());

    // CandidateFilter candidateFilter1 = new CandidateFilter();

    return new ResponseObject(200, "Result for filter candidate", candidateDTOS);
  }

  @GetMapping("/common/get-all-extra-info")
  public ResponseObject getAllExtraInfo() {
    return extraInfoService.getAll();
  }

  @GetMapping("/common/employer/get-employer-by-id/{id}")
  ResponseObject getEmployerById(@PathVariable long id) {
    return employerService.getOne(id);
  }

  @PostMapping("/common/register")
  public ResponseObject registerAccount(@RequestBody UserDTO user) {
    Set<String> roleCodes = new HashSet<>();
    roleCodes.add("user");

    user.setRoleCodes(roleCodes);
    return userService.save(user);
  }

  @GetMapping("/common/get-hot-job-post")
  public ResponseObject getHotJobPost() {
    return jobPostService.getHotJobPost();
  }

  @GetMapping("/common/get-job-post-due-soon")
  public ResponseObject getJobPostDueSoon() {
    return jobPostService.getJobPostDueSoon();
  }

  @GetMapping("/common/get-profile-candidate/{candidateId}")
  ResponseObject getProfileCandidate(@PathVariable long candidateId) {
    return candidateService.getOne(candidateId);
  }

  @GetMapping("/common/get-active-job-post")
  public ResponseObject getActiveJobPost() {
    return jobPostService.getActiveJobPost();
  }

  @GetMapping("/common/get-experience-by-candidate-id/{id}")
  ResponseObject getAllExperienceByCandidateId(@PathVariable(value = "id") long id) {

    return experienceService.getAllExperienceByCandidateId(id);
  }

  @GetMapping("/common/candidate-profile/{candidateId}")
  ResponseObject getCandidateProfile(@PathVariable(value = "candidateId") long candidateId) {
    return candidateService.getOne(candidateId);
  }

  @GetMapping("/common/get-amount-application-to-job-post/{jobPostId}")
  DataResponse getAmountApplicationToEmployer(@PathVariable(value = "jobPostId")long jobPostId) {
    return applicationService.getAmountApplicationByJobPostId(jobPostId);
  }

  @GetMapping("/common/get-job-post-amount")
  public ResponseObject getJobPostAmount() {
    return jobPostService.getJobPostAmount();
  }

  @GetMapping("/common/view-job-post/{jobPostId}")
  public DataResponse viewJobPost(@PathVariable(value = "jobPostId") long jobPostId) {
    return jobPostService.countJobPostViewReturnDataResponse(jobPostId);
  }

  @GetMapping("/common/get-application-amount")
  public ResponseObject getApplicationAmount() {
    return applicationService.getApplicationAmount();
  }

}
