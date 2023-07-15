package hcmute.puzzle.controller;

import hcmute.puzzle.infrastructure.dtos.request.LoginRequest;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.dtos.response.LoginResponse;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.model.MockTestRequest;
import hcmute.puzzle.service.TestService;
import hcmute.puzzle.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	TestService testService;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	String authenticateUser() throws Exception {
		String jwt;
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("admin2@gmail.com");
		loginRequest.setPassword("123456");
		MockTestRequest mockTestRequest = MockTestRequest.builder()
														 .method(HttpMethod.POST)
														 .content(Utils.objectToJson(loginRequest))
														 .url("/auth/login")
														 .build();
		MvcResult mvcResult = testService.processingTestApi(mockMvc, mockTestRequest);
		if (mvcResult.getResponse().getStatus() == 200) {
			Utils<DataResponse<LoginResponse>> loginResponseUtils = new Utils<>();
			String json = new String(mvcResult.getResponse().getContentAsByteArray());
			json = Utils.formatJson(json);
			log.info(json);
			LoginResponse loginResponse = loginResponseUtils.jsonToObjectClass(json).getData();
			return loginResponse.getJwt();
		}
		return null;
	}

	@Test
	void getAllCompany() throws Exception {
		//		User admin = userRepository.getUserByEmailJoinFetch("admin2@gmail.com");
		//		if (admin == null) {
		//			throw new NotFoundDataException("Not found admin user");
		//		}
		//		CustomUserDetails customUserDetails = new CustomUserDetails(admin);
		String jwt = this.authenticateUser();
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("", List.of("skill"));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(jwt);
		MockTestRequest mockTestRequest = MockTestRequest.builder()
														 .method(HttpMethod.GET)
														 .httpHeaders(httpHeaders)
														 .url("/admin/company")
														 .build();
		testService.processingTestApi(mockMvc, mockTestRequest);
	}

	@Test
	void getAllCompanyInactive() {
	}

	@Test
	void getOneCompany() {
	}

	@Test
	void getAllAccount() {
	}

	@Test
	void getAllAccountById() {
	}

	@Test
	void getAllExtraInfo() {
	}

	@Test
	void getAllJobPost() {
	}

	@Test
	void getAccountAmount() {
	}

	@Test
	void getJobPostAmount() {
	}

	@Test
	void getApplicationAmount() {
	}

	@Test
	void getAllInvoice() {
	}

	@Test
	void getTotalRevenue() {
	}

	@Test
	void getAllCategory() {
	}
}