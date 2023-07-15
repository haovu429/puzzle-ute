package hcmute.puzzle.service;

import hcmute.puzzle.model.MockTestRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@Slf4j
@SpringBootTest
@Service
public class TestService {

	public void processingTestApi(MockMvc mockMvc, HttpMethod method, String url) throws Exception {
		MockTestRequest mockTestRequest = MockTestRequest.builder().url(url).method(method).build();
		processingTestApi(mockMvc, mockTestRequest);
	}

	public MvcResult processingTestApi(MockMvc mockMvc, MockTestRequest mockTestRequest) throws Exception {
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
//											.with(user(mockTestRequest.getCustomUserDetails()))
											.content(mockTestRequest.getContent());
		} else {
			request = MockMvcRequestBuilders.request(mockTestRequest.getMethod(), mockTestRequest.getUrl())
											.contentType(mockTestRequest.getMediaType())
											.characterEncoding("UTF-8")
//											.with(user(mockTestRequest.getCustomUserDetails()))
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

}
