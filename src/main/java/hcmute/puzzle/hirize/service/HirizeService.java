package hcmute.puzzle.hirize.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.pathtemplate.ValidationException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.hirize.model.*;
import hcmute.puzzle.infrastructure.entities.JobPost;
import hcmute.puzzle.infrastructure.entities.SystemConfiguration;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.JsonDataRepository;
import hcmute.puzzle.infrastructure.repository.SystemConfigurationRepository;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class HirizeService {

	@Autowired()
	WebClient webClient;

	// https://connect.hirize.hr
	@Value("${hirize.base_url}")
	String baseUrl;

	@Value("${hirize.endpoint.resume_parser}")
	String resumeParserEndpoint;

	@Value("${hirize.endpoint.job_parser}")
	String jobParserEndpoint;

	@Value("${hirize.endpoint.ai_matcher}")
	String aiMatcherEndpoint;

	@Value("${hirize.endpoint.hirize_iq}")
	String hirizeIqEndpoint;

	@Autowired
	SystemConfigurationRepository systemConfigurationRepository;

	@Autowired
	ApplicationRepository applicationRepository;

	@Autowired
	JsonDataRepository jsonDataRepository;

	public HirizeResponse<ParserData> callApiResumeParser(MultipartFile cvFile, String fileName) throws IOException {
		HirizeResponse<ParserData> result = null;
		String json = null;
		if (cvFile != null && !cvFile.isEmpty()) {
			byte[] image = Base64.encodeBase64(cvFile.getBytes(), false);
			String encodedString = new String(image);

			ParserRequestPayload parserRequestPayload = ParserRequestPayload.builder()
																			.payload(encodedString)
																			.file_name(cvFile.getOriginalFilename())
																			.build();

			MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
			SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
																						   Constant.Hirize.HIRIZE_RESUME_PARSER_API_KEY)
																				   .orElseThrow(
																						   () -> new NotFoundDataException(
																								   "Not found configuration for hirize resume parser"));
			// "DbYzemaWGSN2UXkCVR3751F4J6tQhP"
			String parserToken = systemConfiguration.getValue();
			queryParams.add("api_key", parserToken);
			String url = baseUrl.concat(this.resumeParserEndpoint);
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

			result = webClient.post()
							  .uri(uriBuilder.toUriString())
							  .body(Mono.just(parserRequestPayload), ParserRequestPayload.class)
							  .exchangeToMono(response -> {
								  if (response.statusCode().equals(HttpStatus.CREATED)) {
									  return response.bodyToMono(
											  new ParameterizedTypeReference<HirizeResponse<ParserData>>() {
											  });
								  } else if (response.statusCode().is4xxClientError()) {
									  return Mono.just(null);
								  } else {
									  return response.createException().flatMap(Mono::error);
								  }
							  })
							  .block();
			if (result != null) {
				log.info("\nResponse: \n" + Utils.objectToJson(result));
				this.checkPoverty(result.getRemainingCredit());
			} else {
				log.info("\nResponse: null\n");
			}
		}
		return result;
	}

	public HirizeResponse<JobParserData> callApiJobParser(JobParserRequest jobParserRequest) throws IOException {
		HirizeResponse<JobParserData> result = null;
		String json = null;

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
																					   Constant.Hirize.HIRIZE_JOB_PARSER_API_KEY)
																			   .orElseThrow(
																					   () -> new NotFoundDataException("Not found configuration for hirize job parser"));
		String parserToken = systemConfiguration.getValue();
		queryParams.add("api_key", parserToken);
		String url = baseUrl.concat(this.jobParserEndpoint);
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		result = webClient.post()
						  .uri(uriBuilder.toUriString())
						  .body(Mono.just(jobParserRequest), JobParserRequest.class)
						  .exchangeToMono(response -> {
							  if (response.statusCode().equals(HttpStatus.CREATED)) {
								  return response.bodyToMono(
										  new ParameterizedTypeReference<HirizeResponse<JobParserData>>() {
										  });
							  } else if (response.statusCode().is4xxClientError()) {
								  return Mono.just(null);
							  } else {
								  return response.createException().flatMap(Mono::error);
							  }
						  })
						  .block();
		if (result != null) {
			log.info("\nResponse: \n" + Utils.objectToJson(result));
			this.checkPoverty(result.getRemainingCredit());
		} else {
			log.info("\nResponse: null\n");
		}

		return result;
	}

	public HirizeResponse<AIMatcherData> callApiAiMatcher(AIMatcherRequest aiMatcherRequest) {
		HirizeResponse<AIMatcherData> result = null;
		String json = null;

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
																					   Constant.Hirize.HIRIZE_AI_MATCHER_API_KEY)
																			   .orElseThrow(
																					   () -> new NotFoundDataException(
																							   "Not found configuration for hirize AI matcher"));
		String parserToken = systemConfiguration.getValue();
		queryParams.add("api_key", parserToken);
		String url = baseUrl.concat(this.aiMatcherEndpoint);
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		result = webClient.post()
						  .uri(uriBuilder.toUriString())
						  .body(Mono.just(aiMatcherRequest), AIMatcherRequest.class)
						  .exchangeToMono(response -> {
							  if (response.statusCode().equals(HttpStatus.CREATED)) {
								  return response.bodyToMono(
										  new ParameterizedTypeReference<HirizeResponse<AIMatcherData>>() {
										  });
							  } else if (response.statusCode().is4xxClientError()) {
								  //
								  throw new RuntimeException(
										  "Response from hirize has error code: " + response.statusCode());
								  //return Mono.just(null);
							  } else {
								  return response.createException().flatMap(Mono::error);
							  }
						  })
						  .block();
		if (result != null) {
			json = Utils.objectToJson(result);
			log.info("\nResponse: \n" + json);
			this.checkPoverty(result.getRemainingCredit());
		} else {
			log.info("\nResponse: null\n");
		}
		return result;
	}

	public HirizeResponse<HirizeIQData> callApiHirizeIQ(HirizeIQRequest hirizeIQRequest) {
		HirizeResponse<HirizeIQData> result = null;
		String json = null;

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
																					   Constant.Hirize.HIRIZE_HIRIZE_IQ_API_KEY)
																			   .orElseThrow(
																					   () -> new NotFoundDataException(
																							   "Not found configuration for hirize AI matcher"));
		// "zG8U46h79akDe1PY5V3WbSQ2XtCNFm"
		String parserToken = systemConfiguration.getValue();
		queryParams.add("api_key", parserToken);
		String url = baseUrl.concat(this.hirizeIqEndpoint);
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		result = webClient.post()
						  .uri(uriBuilder.toUriString())
						  .body(Mono.just(hirizeIQRequest), HirizeIQRequest.class)
						  .exchangeToMono(response -> {
							  if (response.statusCode().equals(HttpStatus.CREATED)) {
								  return response.bodyToMono(
										  new ParameterizedTypeReference<HirizeResponse<HirizeIQData>>() {
										  });
							  } else if (response.statusCode().is4xxClientError()) {
								  return Mono.just(null);
							  } else {
								  return response.createException().flatMap(Mono::error);
							  }
						  })
						  .block();

		if (result != null) {
			log.info("\nResponse: \n" + Utils.objectToJson(result));
			this.checkPoverty(result.getRemainingCredit());
		} else {
			log.info("\nResponse: null\n");
		}

		return result;
	}

	public void checkPoverty(Double balance) {
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
				Constant.Hirize.HIRIZE_BALANCE).orElse(null);
		//																			   .orElseThrow(
		//																					   () -> new NotFoundDataException(
		//																							   "Not found configuration for hirize AI matcher"));
		if (systemConfiguration != null) {
			systemConfiguration.setValue(String.valueOf(balance));
			systemConfigurationRepository.save(systemConfiguration);
		}
	}

	public static void validateJobParserResponse(HirizeResponse<JobParserData> jobParserResponse, JobPost jobPost) {
		if (jobParserResponse != null && jobParserResponse.getData() != null && jobParserResponse.getData()
																								 .getResult() != null) {
			JobParserResult jobParserResult = jobParserResponse.getData().getResult();
			if (jobParserResult.getSeniority() == null) {
				throw new ValidationException("Can't identify seniority of this job description");
			}

//			// IllegalArgumentException
//			seniorityType = SeniorityType.valueOf(jobParserResult.getSeniority());

			if (jobParserResult.getJobTitle() == null) {
				if (jobPost.getTitle() != null && !jobPost.getTitle().isBlank()) {
					jobParserResult.setJobTitle(jobPost.getTitle());
				} else {
					throw new ValidationException(
							"Can't identify title of this job description and default title is blank");
				}
			}

			if (jobParserResult.getSkills() == null) {
				throw new ValidationException(
						"Can't identify skills of this job description and default title is blank");
			} else if (jobParserResult.getSkills().length == 0) {
				throw new ValidationException(
						"Can't identify skills of this job description and default title is blank");
			}

		}
	}


	public static HirizeResponse<AIMatcherData> jsonToAIMatcher(String json) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		TypeReference<HirizeResponse<AIMatcherData>> typeRef = new TypeReference<HirizeResponse<AIMatcherData>>() {
		};
		HirizeResponse<AIMatcherData> hirizeResponse = objectMapper.readValue(json, typeRef);
		return hirizeResponse;
	}

	public static HirizeResponse<HirizeIQData> jsonToHirizeIQ(String json) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		TypeReference<HirizeResponse<HirizeIQData>> typeRef = new TypeReference<HirizeResponse<HirizeIQData>>() {
		};
		HirizeResponse<HirizeIQData> hirizeResponse = objectMapper.readValue(json, typeRef);
		return hirizeResponse;
	}

	public static String processFileName(String keyValue) {
		String pattern = "yyyy_MM_dd-HH_mm_ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		// System.out.println(date);
		return keyValue.concat(date);
	}

}
