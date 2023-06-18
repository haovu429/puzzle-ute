package hcmute.puzzle.controller;

import hcmute.puzzle.hirize.model.*;
import hcmute.puzzle.hirize.service.HirizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/hirize")
public class HirizeController {

	@Autowired
	HirizeService hirizeService;

	@PostMapping("/resume-parser")
	public HirizeResponse<ParserData> callApiResumeParser(@RequestPart MultipartFile multipartFile) {
		HirizeResponse<ParserData> result;
		try {
			result = hirizeService.callApiResumeParser(multipartFile, "temp.pdf");
			hirizeService.checkPoverty(result.getRemainingCredit());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}

		return result;
	}

	@PostMapping(value = "/ai-matcher", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public HirizeResponse<AIMatcherData> callApiAiMatcher(@RequestPart MultipartFile cvFile,
			@ModelAttribute MyAiMatcherRequest myAiMatcherRequest) {
		try {
			String cvBase64 = HirizeService.fileToBase64(cvFile);
			AIMatcherRequest aIMatcherRequest = AIMatcherRequest.builder()
																.payload(cvBase64)
																.fileName(cvFile.getOriginalFilename())
																.jobTitle(myAiMatcherRequest.getJobTitle())
																.seniority(myAiMatcherRequest.getSeniority())
																.skills(myAiMatcherRequest.getSkills())
																.build();

			HirizeResponse<AIMatcherData> result;
			result = hirizeService.callApiAiMatcher(aIMatcherRequest);
			hirizeService.checkPoverty(result.getRemainingCredit());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping("/hirize-iq")
	public HirizeResponse<HirizeIQData> callApiHirizeIQ(@RequestPart MultipartFile cvFile,
			@ModelAttribute MyHirizeIQRequest myHirizeIQRequest) {
		try {
			String cvBase64 = HirizeService.fileToBase64(cvFile);
			HirizeIQRequest hirizeIQRequest = HirizeIQRequest.builder()
															 .payload(cvBase64)
															 .fileName(cvFile.getOriginalFilename())
															 .jobDescription(myHirizeIQRequest.getJobDescription())
															 .build();

			HirizeResponse<HirizeIQData> result;
			result = hirizeService.callApiHirizeIQ(hirizeIQRequest);
			hirizeService.checkPoverty(result.getRemainingCredit());

			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}

	}
}