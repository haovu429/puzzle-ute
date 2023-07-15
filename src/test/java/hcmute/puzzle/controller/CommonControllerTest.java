package hcmute.puzzle.controller;

import hcmute.puzzle.model.MockTestRequest;
import hcmute.puzzle.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
		//		Utils<JobPostFilterRequest> utils = new Utils<>();
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
		processingTestApi(HttpMethod.GET, "/employer/get-employer-by-id/3");
	}

	@Test
	void registerAccount() throws Exception {
		processingTestApi(HttpMethod.GET, "/employer/get-employer-by-id/3");
	}

	@Test
	void getHotJobPost() {
	}

	@Test
	void getJobPostDueSoon() {
	}

	@Test
	void getProfileCandidate() {
	}

	@Test
	void getActiveJobPost() {
	}

	@Test
	void getAllExperienceByCandidateId() {
	}

	@Test
	void getCandidateProfile() {
	}

	@Test
	void getAmountApplicationToEmployer() {
	}

	@Test
	void getJobPostAmount() {
	}

	@Test
	void viewJobPost() {
	}

	@Test
	void getApplicationAmount() {
	}

	@Test
	void viewBlogPost() {
	}

	@Test
	void likeComment() {
	}

	@Test
	void disLikeComment() {
	}

	@Test
	void getAllBlogPost() {
	}

	@Test
	void getBlogCateWithPostAmount() {
	}

	@Test
	void getAllCategory() {
	}

	@Test
	void getCommentListByJobPostId() {
	}
}