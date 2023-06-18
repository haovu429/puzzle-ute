package hcmute.puzzle.hirize.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.hirize.model.*;
import hcmute.puzzle.infrastructure.entities.SystemConfiguration;
import hcmute.puzzle.infrastructure.repository.SystemConfigurationRepository;
import hcmute.puzzle.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
public class HirizeService {

	@Autowired()
	@Qualifier("hirize")
	WebClient webClient;

	private String baseUrl = "https://connect.hirize.hr";

	@Autowired
	SystemConfigurationRepository systemConfigurationRepository;


	public HirizeResponse callApiResumeParser(MultipartFile cvFile, String fileName) throws IOException {
		HirizeResponse result = null;
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
			String url = baseUrl.concat("/api/public/parser");
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

			result = webClient.post()
							  .uri(uriBuilder.toUriString())
							  .body(Mono.just(parserRequestPayload), ParserRequestPayload.class)
							  .exchangeToMono(response -> {
								  if (response.statusCode().equals(HttpStatus.CREATED)) {
									  return response.bodyToMono(HirizeResponse.class);
								  } else if (response.statusCode().is4xxClientError()) {
									  return Mono.just(null);
								  } else {
									  return response.createException().flatMap(Mono::error);
								  }
							  })
							  .block();
			if (result != null) {
				log.info("\nResponse: \n" + objectToJson(result));
			} else {
				log.info("\nResponse: null\n");
			}
		}
		return result;
	}

	public HirizeResponse callApiAiMatcher(AIMatcherRequest aiMatcherRequest) {
		HirizeResponse result = null;
		String json = null;

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
																					   Constant.Hirize.HIRIZE_AI_MATCHER_API_KEY)
																			   .orElseThrow(
																					   () -> new NotFoundDataException(
																							   "Not found configuration for hirize AI matcher"));
		// "hkSGJF9b2U6Ct51azV843mNWD7XPeQ"
		String parserToken = systemConfiguration.getValue();
		queryParams.add("api_key", parserToken);
		String url = baseUrl.concat("/api/public/ai-matcher");
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		result = webClient.post()
						  .uri(uriBuilder.toUriString())
						  .body(Mono.just(aiMatcherRequest), AIMatcherRequest.class)
						  .exchangeToMono(response -> {
							  if (response.statusCode().equals(HttpStatus.CREATED)) {
								  return response.bodyToMono(HirizeResponse.class);
							  } else if (response.statusCode().is4xxClientError()) {
								  return Mono.just(null);
							  } else {
								  return response.createException().flatMap(Mono::error);
							  }
						  })
						  .block();
		if (result != null) {
			json = objectToJson(result);
			log.info("\nResponse: \n" + json);
		} else {
			log.info("\nResponse: null\n");
		}
		return result;
	}

	public HirizeResponse<HirizeIQData> callApiHirizeIQ(HirizeIQRequest hirizeIQRequest) {
		HirizeResponse result = null;
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
		String url = baseUrl.concat("/api/public/hirize-iq");
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		result = webClient.post()
						  .uri(uriBuilder.toUriString())
						  .body(Mono.just(hirizeIQRequest), HirizeIQRequest.class)
						  .exchangeToMono(response -> {
							  if (response.statusCode().equals(HttpStatus.CREATED)) {
								  return response.bodyToMono(HirizeResponse.class);
							  } else if (response.statusCode().is4xxClientError()) {
								  return Mono.just(null);
							  } else {
								  return response.createException().flatMap(Mono::error);
							  }
						  })
						  .block();

		if (result != null) {
			log.info("\nResponse: \n" + objectToJson(result));
		} else {
			log.info("\nResponse: null\n");
		}

		return result;
	}

	public static String objectToJson(Object object) {
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(object);
			return json;
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String fileToBase64(MultipartFile file) {
		try {
			byte[] image = Base64.encodeBase64(file.getBytes(), false);
			String encodedString = new String(image);
			return encodedString;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
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

}
