package hcmute.puzzle.controller;

import hcmute.puzzle.hirize.model.*;
import hcmute.puzzle.hirize.service.HirizeService;
import hcmute.puzzle.infrastructure.models.cohere.SummarizationResponse;
import hcmute.puzzle.infrastructure.models.cohere.SummarizeRequest;
import hcmute.puzzle.infrastructure.models.translate.TranslateRequest;
import hcmute.puzzle.infrastructure.models.translate.TranslateResponse;
import hcmute.puzzle.services.impl.WebClientService;
import hcmute.puzzle.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/lab")
public class LabController {

	@Autowired
	HirizeService hirizeService;

	@Autowired
	WebClientService webClientService;

	@PostMapping("/hirize/resume-parser")
	public HirizeResponse<ParserData> callApiResumeParser(@RequestPart MultipartFile multipartFile) {
		HirizeResponse<ParserData> result;
		try {
			result = hirizeService.callApiResumeParser(multipartFile, "temp.pdf");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return result;
	}

	@PostMapping("/hirize/job-parser")
	public HirizeResponse<JobParserData> callApiJobParser(@RequestBody JobParserRequest jobParserRequest) {
		HirizeResponse<JobParserData> result;
		try {
			result = hirizeService.callApiJobParser(jobParserRequest);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return result;
	}

	@PostMapping(value = "/hirize/ai-matcher", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public HirizeResponse<AIMatcherData> callApiAiMatcher(@RequestPart MultipartFile cvFile,
			@ModelAttribute MyAiMatcherRequest myAiMatcherRequest) {
		try {
			String cvBase64 = Utils.fileToBase64(cvFile);
			AIMatcherRequest aIMatcherRequest = AIMatcherRequest.builder()
																.payload(cvBase64)
																.fileName(cvFile.getOriginalFilename())
																.jobTitle(myAiMatcherRequest.getJobTitle())
																.seniority(myAiMatcherRequest.getSeniority())
																.skills(myAiMatcherRequest.getSkills())
																.build();

			HirizeResponse<AIMatcherData> result;
			result = hirizeService.callApiAiMatcher(aIMatcherRequest);
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping("/hirize/hirize-iq")
	public HirizeResponse<HirizeIQData> callApiHirizeIQ(@RequestPart MultipartFile cvFile,
			@ModelAttribute MyHirizeIQRequest myHirizeIQRequest) {
		try {
			String cvBase64 = Utils.fileToBase64(cvFile);
			HirizeIQRequest hirizeIQRequest = HirizeIQRequest.builder()
															 .payload(cvBase64)
															 .fileName(cvFile.getOriginalFilename())
															 .jobDescription(myHirizeIQRequest.getJobDescription())
															 .build();

			HirizeResponse<HirizeIQData> result;
			result = hirizeService.callApiHirizeIQ(hirizeIQRequest);

			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping("/cohere/summarize")
	public SummarizationResponse callApiSummarizeToCohere(@RequestBody SummarizeRequest summarizeRequest) {
		SummarizationResponse result;
		try {
			result = webClientService.callApiSummarizeToCohere(summarizeRequest);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return result;
	}

	@PostMapping("/translate")
	public TranslateResponse callApiTranslateCustom(@RequestBody TranslateRequest translateRequest) {
		TranslateResponse result;
		try {
			result = webClientService.callApiTranslateCustom(translateRequest);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return result;
	}

}