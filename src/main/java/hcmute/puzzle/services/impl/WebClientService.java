package hcmute.puzzle.services.impl;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.entities.SystemConfiguration;
import hcmute.puzzle.infrastructure.models.cohere.SummarizationResponse;
import hcmute.puzzle.infrastructure.models.cohere.SummarizeRequest;
import hcmute.puzzle.infrastructure.models.translate.TranslateRequest;
import hcmute.puzzle.infrastructure.models.translate.TranslateResponse;
import hcmute.puzzle.infrastructure.repository.SystemConfigurationRepository;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class WebClientService {

	@Autowired()
	WebClient webClient;

	// https://connect.hirize.hr
	@Value("${cohere.base_url}")
	String cohereBaseUrl;

	@Value("${cohere.endpoint.summarize}")
	String summarizeEndpoint;

	@Value("${cohere.api_key}")
	String cohereApiKey;

	@Value("${translate.end_point}")
	String translateEndPoint;

	@Value("${detect.language.api_key}")
	String detectLanguageApiKey;

	@Autowired
	SystemConfigurationRepository systemConfigurationRepository;

	// Call api summarization from Cohere.ai
	public SummarizationResponse callApiSummarizeToCohere(SummarizeRequest summarizeRequest) {
		log.info("Call api Summarize");
		SummarizationResponse result = null;

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		String checkApiKey = null;
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
				Constant.Cohere.COHERE_API_KEY).orElse(null);
		//		String parserToken = systemConfiguration.getValue();
		//		queryParams.add("api_key", parserToken);

		if (systemConfiguration != null && systemConfiguration.getValue() != null) {
			checkApiKey = systemConfiguration.getValue();
		} else if (cohereApiKey != null && !cohereApiKey.isBlank()) {
			checkApiKey = cohereApiKey;
		} else {
			throw new NotFoundDataException("Not found configuration for Cohere api");
		}

		// use in lambda
		final String apiKey = checkApiKey;

		String url = cohereBaseUrl.concat(this.summarizeEndpoint);
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		result = webClient.post()
						  .uri(uriBuilder.toUriString())
						  .headers(headers -> headers.setBearerAuth(apiKey))
						  .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						  .body(Mono.just(summarizeRequest), SummarizeRequest.class)
						  .exchangeToMono(response -> {
							  if (response.statusCode().equals(HttpStatus.OK)) {
								  return response.bodyToMono(SummarizationResponse.class);
							  } else if (response.statusCode().is4xxClientError()) {
								  return Mono.just(null);
							  } else {
								  return response.createException().flatMap(Mono::error);
							  }
						  })
						  .block();
		if (result != null) {
			log.info("\nResponse: \n" + Utils.objectToJson(result));
		} else {
			log.info("\nResponse: null\n");
		}

		return result;
	}

	// Call api summarization to App Script google
	public TranslateResponse callApiTranslateCustom(TranslateRequest translateRequest) throws APIError {
		log.info("Call api Summarize");
		log.info("Request : {}", Utils.objectToJson(translateRequest));
		// Pre-process request
		SystemConfiguration detectLanguageEnable = systemConfigurationRepository.findByKey(
				Constant.DetectLanguage.DETECT_LANGUAGE_ENABLE).orElse(null);
		if (detectLanguageEnable != null && detectLanguageEnable.getValue() != null) {
			log.info("Detect language enable configuration from DB: {}", detectLanguageEnable.getValue());
			boolean detectEnable = Boolean.parseBoolean(detectLanguageEnable.getValue());
			if (detectEnable) {
				String updateFromLanguage = this.detectLanguage(translateRequest.getData()[0].getOriginal());
				translateRequest.setFrom(updateFromLanguage);
			}
		}

		TranslateResponse result = null;

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		SystemConfiguration translateEndPointConfig = systemConfigurationRepository.findByKey(
				Constant.Translate.TRANSLATE_END_POINT).orElse(null);
		//		String parserToken = systemConfiguration.getValue();
		//		queryParams.add("api_key", parserToken);

		if (translateEndPointConfig != null && translateEndPointConfig.getValue() != null) {
			translateEndPoint = translateEndPointConfig.getValue();
		} else if (!(translateEndPoint != null && !translateEndPoint.isBlank())) {
			throw new NotFoundDataException("Not found configuration for Cohere api");
		}

		String url = translateEndPoint;
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		ResponseEntity<Void> responseEntityForPost = null;
		ResponseEntity<TranslateResponse> responseEntityForGet = null;

		responseEntityForPost = webClient.post()
										 .uri(uriBuilder.toUriString())
										 .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
										 .body(Mono.just(translateRequest), TranslateRequest.class)
										 .exchangeToMono(ClientResponse::toBodilessEntity)
										 .block();

		// Follow redirect
		if (responseEntityForPost != null) {
			if (responseEntityForPost.getStatusCode().is3xxRedirection()) {
				URI redirectUrl = responseEntityForPost.getHeaders().getLocation();
				if (redirectUrl != null) {
					responseEntityForGet = webClient.get()
													.uri(redirectUrl)
													.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
													.exchangeToMono(
															response -> response.toEntity(TranslateResponse.class))
													.block();
				}
			}
		}
		if (responseEntityForGet != null) {
			result = responseEntityForGet.getBody();
		}

		if (result != null) {
			log.info("\nResponse: \n" + Utils.objectToJson(result));
		} else {
			log.info("\nResponse: null\n");
		}

		// Check result belong to Success or Fail
		if (!result.getMessage().equals("Success")) {
			throw new CustomException("Translate fail");
		}
		return result;
	}

	public String detectLanguage(String text) throws APIError {
		SystemConfiguration systemConfiguration = systemConfigurationRepository.findByKey(
				Constant.DetectLanguage.DETECT_LANGUAGE_API_KEY).orElse(null);
		if (systemConfiguration != null && systemConfiguration.getValue() != null && !systemConfiguration.getValue().isBlank()) {
			detectLanguageApiKey = systemConfiguration.getValue();
		} else if (!(detectLanguageApiKey != null && !detectLanguageApiKey.isBlank())) {
			throw new NotFoundDataException("Not found configuration for Cohere api");
		}
		DetectLanguage.apiKey = detectLanguageApiKey;

		// Enable secure mode (SSL) if passing sensitive information
		DetectLanguage.ssl = true;

		// Detect
		List<Result> results = DetectLanguage.detect(text);
		Result result = results.get(0);
		log.info("Detect result: " + Utils.objectToJson(result));
		return result.language.toUpperCase();
	}
}
