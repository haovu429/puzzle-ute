package hcmute.puzzle.services.impl;

import com.detectlanguage.errors.APIError;
import com.fasterxml.jackson.core.JsonProcessingException;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.hirize.model.*;
import hcmute.puzzle.hirize.service.HirizeService;
import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.EmployerMapper;
import hcmute.puzzle.infrastructure.models.enums.JsonDataType;
import hcmute.puzzle.infrastructure.repository.*;
import hcmute.puzzle.services.EmployerService;
import hcmute.puzzle.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class EmployerServiceImpl implements EmployerService {

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	EmployerRepository employerRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JobPostRepository jobPostRepository;

	@Autowired
	ApplicationRepository applicationRepository;

	@Autowired
	RoleRepository roleRepository;

	//  @Autowired
	//  Converter converter;

	@Autowired
	EmployerMapper employerMapper;

	@PersistenceContext
	public EntityManager em;

	@Autowired
	HirizeService hirizeService;

	@Autowired
	JsonDataRepository jsonDataRepository;

	@Value("${file.location.download}")
	String tempFileLocation;

	@Autowired
	SystemConfigurationRepository systemConfigurationRepository;

	@Autowired
	WebClientService webClientService;

	@Override
	public EmployerDto save(EmployerDto employerDTO) {
		// casting provinceDTO to ProvinceEntity
		Employer employer = employerMapper.employerDtoToEmployer(employerDTO);

		// save province
		//    employer.setId(0);
		if (employer.getUser().getCandidate() != null) {
			throw new RuntimeException("This account for candidate");
		}

		employerRepository.save(employer);

		User user = userRepository.findById(employer.getId())
								  .orElseThrow(() -> new NotFoundDataException("Account isn't exist"));

		Role role = roleRepository.findById("employer")
								  .orElseThrow(() -> new NotFoundDataException("role candidate isn't exist"));

		user.getRoles().add(role);
		userRepository.save(user);

		EmployerDto employerDto = employerMapper.employerToEmployerDto(employer);

		return employerDto;
	}

	@Override
	public void delete(long id) {
		Employer employer = employerRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found employer"));
		employerRepository.delete(employer);
	}

	@Override
	public EmployerDto update(EmployerDto employerDTO) {
		Employer employer = employerRepository.findById(employerDTO.getId()).orElseThrow(() -> new NotFoundDataException("Not found employer"));
		employerMapper.updateEmployerFromEmployerDto(employerDTO, employer);
		return employerMapper.employerToEmployerDto(employer);
	}

	@Override
	public EmployerDto getOne(long id) {
		Employer employer = employerRepository.findById(id)
											  .orElseThrow(() -> new NotFoundDataException(
													  "Cannot find employer with id = " + id));

		return employerMapper.employerToEmployerDto(employer);
	}

	@Override
	public List<EmployerDto> getEmployerFollowedByCandidateId(long candidateId) {
		Candidate candidate = candidateRepository.findById(candidateId).orElseThrow(() -> new NotFoundDataException("Candidate isn't exist"));


		List<EmployerDto> employerDtos = candidate.getFollowingEmployers()
												  .stream()
												  .map(employerMapper::employerToEmployerDto)
												  .toList();

		return employerDtos;
	}

	//markingJobpostWasDeleted

	@Override
	public double getApplicationRateEmployerId(long employerId) {
		Optional<Employer> employer = employerRepository.findById(employerId);
		if (employer.isEmpty()) {
			throw new CustomException("Cannot find employer with id = " + employerId);
		}

		double rate = 0; // mac dinh, hoi sai neu view = 0 và application > 0
		long viewOfCandidateAmount = getViewedCandidateAmountToJobPostCreatedByEmployer(employerId);
		long applicationOfCandidateAmount = applicationRepository.getAmountApplicationToEmployer(employerId);

		if (viewOfCandidateAmount != 0 && viewOfCandidateAmount >= applicationOfCandidateAmount) {
			rate = Double.valueOf(applicationOfCandidateAmount) / viewOfCandidateAmount;
			rate = rate * 100; // doi ti le ra phan tram
			// lam tron 2 chu so thap phan
			rate = Math.round(rate * 100.0) / 100.0;
		}

		return rate;
	}

	public long getViewedCandidateAmountToJobPostCreatedByEmployer(long employerId) {
		long amount = 0;
		// check subscribes of employer
		String sql = "SELECT COUNT(u.id) FROM JobPost jp INNER JOIN jp.viewedUsers u WHERE jp.createdEmployer.id =:employerId AND u.candidate.id IS NOT NULL AND jp.isDeleted=FALSE";
		try {
			amount = (long) em.createQuery(sql).setParameter("employerId", employerId).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}

	@Transactional
	public HirizeResponse<AIMatcherData> getPointOfApplicationFromHirize(Long jobPostId, Long candidateId) throws
			IOException, APIError {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		Application application = applicationRepository.findApplicationByCanIdAndJobPostId(candidateId, jobPostId)
													   .orElseThrow(() -> new NotFoundDataException(
															   "Not found application"));

		HirizeResponse<AIMatcherData> result = null;
		result = this.getScoreAlreadyCalculated(application.getId());
		if (result == null) {
			result = this.getPointOfApplicationFromHirize(application, jobPost);
			if (result != null) {
				// Save result to database
				JsonData jsonData = JsonData.builder()
											.hirizeId(result.getData().getId())
											.applicationId(application.getId())
											.type(JsonDataType.HIRIZE_AI_MATCHER)
											.data(Utils.objectToJson(result))
											.build();
				jsonDataRepository.save(jsonData);
			}
		}
		return result;
	}

	public HirizeResponse<AIMatcherData> getScoreAlreadyCalculated(long applicationId) throws JsonProcessingException {
		Application application = applicationRepository.findById(applicationId)
													   .orElseThrow(() -> new NotFoundDataException(
															   "Not found application"));

		List<JsonData> jsonDataList = jsonDataRepository.findAllByApplicationIdAndType(application.getId(),
																					   JsonDataType.HIRIZE_AI_MATCHER);
		if (jsonDataList != null && !jsonDataList.isEmpty()) {
			JsonData jsonData = jsonDataList.get(0);
			HirizeResponse<AIMatcherData> hirizeResponses = HirizeService.jsonToAIMatcher(jsonData.getData());
			return hirizeResponses;
		}
		return null;
	}

	public HirizeResponse<AIMatcherData> getPointOfApplicationFromHirize(Application application,
			JobPost jobPost) throws IOException, IllegalArgumentException, APIError {

		String cvBase64 = EmployerServiceImpl.getStringBase64FromURL(application.getCv(), tempFileLocation);

		// Detect language of content cv to convert to English
		String translated = webClientService.translate(jobPost.getDescription());
		jobPost.setDescription(translated);

		// Call job parser of Hirize
		JobParserRequest jobParserRequest = JobParserRequest.builder().description(jobPost.getDescription()).build();
		HirizeResponse<JobParserData> jobParserResponse = hirizeService.callApiJobParser(jobParserRequest);
		//SeniorityType seniorityType = SeniorityType.valueOf(jobParserResponse.getData().getResult().getSeniority());
		// Validate job parser result
		HirizeService.validateJobParserResponse(jobParserResponse, jobPost);

		// String[] skillFake = {"JAVA", "HTML", "PYTHON"};
		AIMatcherRequest aIMatcherRequest = AIMatcherRequest.builder()
															.payload(cvBase64)
															.fileName(application.getCvName())
															.jobTitle(jobParserResponse.getData()
																					   .getResult()
																					   .getJobTitle())
															.seniority(jobParserResponse.getData()
																						.getResult()
																						.getSeniority())
															.skills(jobParserResponse.getData().getResult().getSkills())
															.build();

		HirizeResponse<AIMatcherData> result;
		result = hirizeService.callApiAiMatcher(aIMatcherRequest);
		return result;
	}

	public void clearAIMatcherDataForApplication(Long jobPostId, Long candidateId) {
		this.clearJsonDataForApplication(jobPostId, candidateId, JsonDataType.HIRIZE_AI_MATCHER);
	}

	public void clearHirizeIQDataForApplication(Long jobPostId, Long candidateId) {
		this.clearJsonDataForApplication(jobPostId, candidateId, JsonDataType.HIRIZE_IQ);
	}

	public void clearJsonDataForApplication(Long jobPostId, Long candidateId, JsonDataType jsonDataType) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		Application application = applicationRepository.findApplicationByCanIdAndJobPostId(candidateId, jobPost.getId())
													   .orElseThrow(() -> new NotFoundDataException(
															   "Not found application"));

		List<JsonData> jsonDataList = jsonDataRepository.findAllByApplicationIdAndType(application.getId(),
																					   jsonDataType);
		jsonDataRepository.deleteAll(jsonDataList);
	}

	public boolean checkAIMatcherExisted(Long jobPostId, Long candidateId) {
		return this.checkJsonDataExistedByType(jobPostId, candidateId, JsonDataType.HIRIZE_AI_MATCHER);
	}

	public boolean checkHirizeIQExisted(Long jobPostId, Long candidateId) {
		return this.checkJsonDataExistedByType(jobPostId, candidateId, JsonDataType.HIRIZE_IQ);
	}

	public boolean checkJsonDataExistedByType(Long jobPostId, Long candidateId, JsonDataType jsonDataType) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		Application application = applicationRepository.findApplicationByCanIdAndJobPostId(candidateId, jobPost.getId())
													   .orElseThrow(() -> new NotFoundDataException(
															   "Not found application"));

		List<JsonData> jsonDataList = jsonDataRepository.findAllByApplicationIdAndType(application.getId(),
																					   jsonDataType);
		if (jsonDataList.isEmpty()) {
			return false;
		}
		return true;
	}


	@Transactional
	public HirizeResponse<HirizeIQData> getAISuggestForApplicationFromHirize(Long jobPostId, Long candidateId) throws
			IOException, APIError {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NotFoundDataException("Not found job post"));
		Application application = applicationRepository.findApplicationByCanIdAndJobPostId(candidateId, jobPostId)
													   .orElseThrow(() -> new NotFoundDataException(
															   "Not found application"));

		HirizeResponse<HirizeIQData> result = null;
		result = this.getAISuggestAlreadyExisted(application.getId());
		if (result == null) {
			result = this.getAISuggestForApplicationFromHirize(application, jobPost);
			if (result != null) {
				// Save result to database
				JsonData jsonData = JsonData.builder()
											.hirizeId(result.getData().getId())
											.applicationId(application.getId())
											.type(JsonDataType.HIRIZE_AI_MATCHER)
											.data(Utils.objectToJson(result))
											.build();
				jsonDataRepository.save(jsonData);
			}
		}
		return result;
	}

	public HirizeResponse<HirizeIQData> getAISuggestAlreadyExisted(long applicationId) throws JsonProcessingException {
		Application application = applicationRepository.findById(applicationId)
													   .orElseThrow(() -> new NotFoundDataException(
															   "Not found application"));

		List<JsonData> jsonDataList = jsonDataRepository.findAllByApplicationIdAndType(application.getId(),
																					   JsonDataType.HIRIZE_IQ);
		if (jsonDataList != null && !jsonDataList.isEmpty()) {
			JsonData jsonData = jsonDataList.get(0);
			HirizeResponse<HirizeIQData> hirizeResponses = HirizeService.jsonToHirizeIQ(jsonData.getData());
			return hirizeResponses;
		}
		return null;
	}

	public HirizeResponse<HirizeIQData> getAISuggestForApplicationFromHirize(Application application,
			JobPost jobPost) throws IOException, IllegalArgumentException, APIError {
		String cvBase64 = EmployerServiceImpl.getStringBase64FromURL(application.getCv(), tempFileLocation);

		// Detect language of content cv to convert to English
		String translated = webClientService.translate(jobPost.getDescription());
		jobPost.setDescription(translated);

		// String[] skillFake = {"JAVA", "HTML", "PYTHON"};
		HirizeIQRequest hirizeIQRequest = HirizeIQRequest.builder()
														 .payload(cvBase64)
														 .fileName(application.getCvName())
														 .jobDescription(jobPost.getDescription())
														 .build();

		HirizeResponse<HirizeIQData> result;
		result = hirizeService.callApiHirizeIQ(hirizeIQRequest);
		return result;
	}

	private static String getStringBase64FromURL(String url, String tempFileLocation) throws IOException {
		String fileName = UUID.randomUUID().toString();
		String fileExtension = "";

		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
			fileExtension = fileName.substring(dotIndex + 1);
			fileName = fileName.substring(0, dotIndex);
		}
		String filePath = tempFileLocation.concat("/")
										  .concat(HirizeService.processFileName(fileName))
										  .concat(".")
										  .concat(fileExtension);
		java.io.File tmpFile = new File(filePath);

		Utils.downloadFileFromUrl(tmpFile, url);
		String strBase64 = Utils.fileToBase64(tmpFile);
		if (!tmpFile.delete()) {
			log.error("Failed to delete the file");
		}
		return strBase64;
	}

}
