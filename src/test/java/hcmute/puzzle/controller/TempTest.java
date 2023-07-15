package hcmute.puzzle.controller;

import hcmute.puzzle.PuzzleUteApplication;
import hcmute.puzzle.infrastructure.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = PuzzleUteApplication.class)
@ActiveProfiles(value = "prod")
public class TempTest {

//	@LocalServerPort
//	private int port;

	private String baseUrl = "https://localhost/api";

	private static RestTemplate restTemplate;
	@Test
	void contextLoads() {}

	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}

//	@Autowired
//	private MockMvc mockMvc;

	private String buildEndPoint(String endPoint) {
		return baseUrl.concat(endPoint);
	}

	@Test
	void getOneCompany() throws Exception {
//		mockMvc.perform(MockMvcRequestBuilders.get("/api/job-post/get-all").contentType(MediaType.APPLICATION_JSON))
//			   .andExpect(MockMvcResultMatchers.status().isOk());
		restTemplate.getForObject(buildEndPoint("/job-post/get-all"), List.class);
	}

}
