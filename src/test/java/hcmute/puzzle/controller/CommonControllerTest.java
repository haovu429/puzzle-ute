package hcmute.puzzle.controller;

import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.models.JobPostFilterRequest;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.model.MockTestRequest;
import hcmute.puzzle.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;
import java.util.List;


//@WebMvcTest
//@WebMvcTest(CommonController.class)
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class CommonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	//	@MockBean
	//	private EmployeeService service;

//	@MockBean
	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private WebApplicationContext context;
	//		@BeforeEach
	//		void setUp() {
	//			mockMvc = MockMvcBuilders
	//					.webAppContextSetup(context)
	//					.defaultRequest(get("/").with(user("user").roles("ADMIN")))
	//					.apply(springSecurity())
	//					.build();
	//		}
	//
	//	@AfterEach
	//	void tearDown() {
	//	}

	private void processingTestApi(HttpMethod method, String url) throws Exception {
		MockTestRequest mockTestRequest = MockTestRequest.builder().url(url).method(method).build();
		processingTestApi(mockTestRequest);
	}

	private MvcResult processingTestApi(MockTestRequest mockTestRequest) throws Exception {
		log.info("Testing end point: {}", mockTestRequest.getUrl());
		if (mockTestRequest.getParam() == null) {
			mockTestRequest.setParam(new LinkedMultiValueMap<>());
		}
		MockHttpServletRequestBuilder request = null;
		if (mockTestRequest.getContent() != null && !mockTestRequest.getContent().isBlank()) {
			request = MockMvcRequestBuilders.request(mockTestRequest.getMethod(), mockTestRequest.getUrl())
											.contentType(mockTestRequest.getMediaType())
											.characterEncoding("UTF-8")
											.params(mockTestRequest.getParam())
											.content(mockTestRequest.getContent());
		} else {
			request = MockMvcRequestBuilders.request(mockTestRequest.getMethod(), mockTestRequest.getUrl())
											.contentType(mockTestRequest.getMediaType())
											.characterEncoding("UTF-8")
											.params(mockTestRequest.getParam());
		}

		MvcResult mvcResul = mockMvc.perform(request)
									.andExpect(MockMvcResultMatchers.status().isOk())
									.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
									.andReturn();
		log.info("Status: {}", mvcResul.getResponse().getStatus());
		if (mvcResul.getResponse().getStatus() != 200) {
			log.info("Detail response: {}", mvcResul.getResponse().getContentAsString());
		}
		return mvcResul;
	}

	@Test
	void getAllJobPost() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/job-post/get-all");
	}

	@Test
	void getJobPostById() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/job-post/get-one/3");
	}

	@Test
	void getAllCompany() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/company");
	}

	@Test
	void getOneCompany() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/company/get-one-company/2");
	}

	@Test
	void getAllExtraInfoByType() throws Exception {
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("type", List.of("skill"));
		MockTestRequest mockTestRequest = MockTestRequest.builder()
														 .url("/common/get-all-extra-info-by-type")
														 .method(HttpMethod.GET)
														 .param(params)
														 .build();
		processingTestApi(mockTestRequest);
	}

	@Test
	void filterJobPostV2() {
	}

	@Test
	void filterJobPost() throws Exception {
		String path = "src/main/resources/test/payload/job_post_filter.json";
		File file = new File(path);
		String content = new String(Files.readAllBytes(file.toPath()));
				Utils<JobPostFilterRequest> utils = new Utils<>();
		//		JobPostFilterRequest jobPostFilterRequest = utils.jsonToObjectClass(content);
		MockTestRequest mockTestRequest = MockTestRequest.builder()
														 .url("/common/job-post-filter")
														 .method(HttpMethod.POST)
														 .content(content)
														 .mediaType(MediaType.APPLICATION_JSON)
														 .build();
		MvcResult mvcResult = processingTestApi(mockTestRequest);
		log.info(Utils.formatJson(new String(mvcResult.getResponse().getContentAsByteArray())));
	}

	@Test
	void filterCandidate() {

	}

	@Test
	void getAllExtraInfo() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/get-all-extra-info");
	}

	@Test
	void getEmployerById() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/employer/get-employer-by-id/3");
	}

//	@Test
//	void registerAccount() throws Exception {
//		processingTestApi(HttpMethod.GET, "/employer/get-employer-by-id/3");
//	}

	@Test
	void getHotJobPost() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/get-hot-job-post");
	}

	@Test
	void getJobPostDueSoon() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/get-hot-job-post");
	}

	@Test
	void getProfileCandidate() throws Exception {
		Pageable pageable = Pageable.ofSize(5).withPage(0);
		Page<Candidate> candidates = candidateRepository.findAll(pageable);
		Candidate candidateTest = null;
		if (!candidates.isEmpty()) {
			candidateTest = candidates.getContent().get(0);
		} else {
			throw new NotFoundDataException("Not found candidate");
		}
		processingTestApi(HttpMethod.GET, "/common/candidate-profile/" + candidateTest.getId());

	}

	@Test
	void getActiveJobPost() throws Exception {
		processingTestApi(HttpMethod.GET, "/common/get-active-job-post");
	}

//	@Test
//	void getAllExperienceByCandidateId() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getCandidateProfile() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getAmountApplicationToEmployer() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getJobPostAmount() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void viewJobPost() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getApplicationAmount() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void viewBlogPost() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void likeComment() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void disLikeComment() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getAllBlogPost() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getBlogCateWithPostAmount() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getAllCategory() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
//
//	@Test
//	void getCommentListByJobPostId() {
//		processingTestApi(HttpMethod.GET, "/employer/get-hot-job-post");
//	}
}