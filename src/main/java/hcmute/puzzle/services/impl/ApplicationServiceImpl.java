package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.dtos.response.CandidateApplicationResult;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.ApplicationMapper;
import hcmute.puzzle.infrastructure.mappers.CandidateMapper;
import hcmute.puzzle.infrastructure.models.ApplicationResult;
import hcmute.puzzle.infrastructure.models.CandidateAppliedAndResult;
import hcmute.puzzle.infrastructure.models.ResponseApplication;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.EmployerRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.services.ApplicationService;
import hcmute.puzzle.utils.mail.MailObject;
import hcmute.puzzle.utils.mail.SendMail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {
	@Autowired
	ApplicationRepository applicationRepository;

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	JobPostRepository jobPostRepository;

	@Autowired
	EmployerRepository employerRepository;

	@Autowired
	ApplicationMapper applicationMapper;

	@Autowired
	CandidateMapper candidateMapper;
	@PersistenceContext
	public EntityManager em;

	@Override
	public ApplicationDto findById(Long id) {
		Application application = applicationRepository.findById(id)
													   .orElseThrow(() -> new CustomException(
															   "Can not found Address with id " + id));
		ApplicationDto applicationDto = applicationMapper.applicationToApplicationDto(application);
		return applicationDto;
	}

	@Override
	public void deleteById(Long id) {
		Application application = applicationRepository.findById(id)
													   .orElseThrow(() -> new RuntimeException(
															   "Can not found Application with id " + id));
		applicationRepository.delete(application);
	}

	@Override
	public Page<ApplicationDto> findAll(Pageable pageable) {

		Page<Application> applications = applicationRepository.findAll(pageable);
		Page<ApplicationDto> applicationDtos = applications.map(applicationMapper::applicationToApplicationDto);
		return applicationDtos;
	}

	@Override
	// Candidate apply jobPost
	public ApplicationDto applyJobPost(long candidateId, long jobPostId) {
		Candidate candidate = candidateRepository.findById(candidateId)
												 .orElseThrow(() -> new NoSuchElementException(
														 "Candidate no value present"));
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new NoSuchElementException("Employer no value present"));
		Application application = new Application();
		application.setCandidate(candidate);
		application.setJobPost(jobPost);
		applicationRepository.save(application);
		ApplicationDto applicationDto = applicationMapper.applicationToApplicationDto(application);
		return applicationDto;
	}

	@Override
	public ApplicationDto responseApplication(long applicationId, ApplicationResult applicationResult) {
		Application application = applicationRepository.findById(applicationId)
													   .orElseThrow(() -> new NotFoundDataException(
															   "Application isn't exist"));
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																					   .getAuthentication()
																					   .getPrincipal();
		// check rights
		User currentUser = customUserDetails.getUser();
		if (currentUser.getEmployer().getId() != application.getJobPost().getCreatedEmployer().getId()) {
			throw new UnauthorizedException("You don't have rights for this application");
		}

		if (applicationResult.getResult()) {
			application.setResult("ACCEPT");
		} else {
			application.setResult("REJECT");
		}
		application.setNote(applicationResult.getNote());
		applicationRepository.save(application);
		ApplicationDto applicationDto = applicationMapper.applicationToApplicationDto(application);
		return applicationDto;
	}

	@Override
	public ApplicationDto responseApplicationByCandidateAndJobPost(ResponseApplication responseApplication) {

		Application application = null;
		if (responseApplication.getApplicationId() != null) {
			application = applicationRepository.findById(responseApplication.getApplicationId())
											   .orElseThrow(() -> new NotFoundDataException("Application isn't exist"));
		} else if (responseApplication.getCandidateId() != null && responseApplication.getJobPostId() != null) {
			application = applicationRepository.findApplicationByCanIdAndJobPostId(responseApplication.getCandidateId(),
																				   responseApplication.getJobPostId())
											   .orElseThrow(() -> new NotFoundDataException("Application isn't exist"));
		} else {
			throw new NotFoundDataException("Not found application");
		}

		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																					   .getAuthentication()
																					   .getPrincipal();
		// check rights
		User currentUser = customUserDetails.getUser();
		if (currentUser.getEmployer().getId() != application.getJobPost().getCreatedEmployer().getId()) {
			throw new UnauthorizedException("You don't have rights for this application");
		}
		if (responseApplication.isResult()) {
			application.setResult("ACCEPT");
		} else {
			application.setResult("REJECT");
		}
		String contentMail = "Result: " + application.getResult() + "\nEmail HR contact: " + responseApplication.getEmail() + "\n" + responseApplication.getNote();
		MailObject mailObject = new MailObject(application.getCandidate().getEmailContact(),
											   responseApplication.getSubject(), contentMail, null);

		Runnable myRunnable = new Runnable() {
			public void run() {
				SendMail.sendMail(mailObject);
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();

		application.setNote(responseApplication.getNote());
		applicationRepository.save(application);
		ApplicationDto applicationDto = applicationMapper.applicationToApplicationDto(application);
		return applicationDto;
	}

	public Page<ApplicationDto> getApplicationByJobPostId(long jobPostId, Pageable pageable) {
		if (!jobPostRepository.existsById(jobPostId)) {
			throw new CustomException("Job Post isn't exists");
		}
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new CustomException("Job post isn't exists"));
		// check rights
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																				 .getAuthentication()
																				 .getPrincipal();
		User currentUser = userDetails.getUser();
		if (jobPost.getCreatedEmployer().getId() != currentUser.getId() && !userDetails.getIsAdmin()) {
			throw new CustomException("You don't have rights for this job post");
		}
		Page<Application> applications = applicationRepository.findApplicationByJobPostId(jobPostId, pageable);
		Page<ApplicationDto> applicationDtos = applications.map(applicationMapper::applicationToApplicationDto);
		return applicationDtos;
	}

	public Page<CandidateApplicationResult> getCandidateAppliedToJobPostIdAndResult(long jobPostId, Pageable pageable) {
		//    String sql =
		//        "SELECT ap, can, jp.name FROM ApplicationEntity ap, CandidateEntity can, JobPostEntity jp  WHERE ap.candidateEntity.id=can.id AND ap.jobPostEntity.id=jp.id AND ap.jobPostEntity.id=:jobPostId";
		//    // Join example with addEntity and addJoin
		//    List<Object[]> rows = em.createQuery(sql).setParameter("jobPostId", jobPostId).getResultList();
		//    //    for (Object[] row : rows) {
		//    //      for(Object obj : row) {
		//    //        System.out.print(obj + "::");
		//    //      }
		//    //      System.out.println("\n");
		//    //    }
		//    // Above join returns both Employee and Address Objects in the array
		//    List<Map<String, Object>> response = new ArrayList<>();
		//    for (Object[] row : rows) {
		//      Map<String, Object> candidateAndResult = new HashMap<>();
		//      String position = (String) row[2];
		//      ApplicationEntity application = (ApplicationEntity) row[0];
		//      System.out.println("Application Info::" + application);
		//      CandidateEntity candidate = (CandidateEntity) row[1];
		//      System.out.println("Candidate Info::" + candidate);
		//      candidateAndResult.put("position", position);
		//      candidateAndResult.put("candidate", converter.toDTO(candidate));
		//      candidateAndResult.put("application", converter.toDTO(application));
		//      response.add(candidateAndResult);
		//    }
		JobPost jobPost = jobPostRepository.findById(jobPostId)
										   .orElseThrow(() -> new CustomException("Job Post isn't exists"));
		String position = jobPost.getTitle();

		return applicationRepository.findApplicationByJobPostId(jobPostId, pageable).map(application -> {
			Candidate candidate = application.getCandidate();
			return CandidateApplicationResult.builder()
											 .position(position)
											 .candidate(candidateMapper.candidateToCandidateDto(candidate))
											 .application(applicationMapper.applicationToApplicationDto(application))
											 .build();
		});
	}

	public List<CandidateApplicationResult> getCandidateAppliedToEmployerAndResult(long employerId) {
		List<CandidateApplicationResult> candidateApplicationResults = applicationRepository.findApplicationByEmployerId(
				employerId).stream().map(application -> {
			Candidate candidate = application.getCandidate();
			CandidateApplicationResult candidateApplicationResult = CandidateApplicationResult.builder()
																							  .position(
																									  application.getJobPost()
																												 .getTitle())
																							  .candidate(
																									  candidateMapper.candidateToCandidateDto(
																											  candidate))
																							  .application(
																									  applicationMapper.applicationToApplicationDto(
																											  application))
																							  .build();
			return candidateApplicationResult;
		}).collect(Collectors.toList());
		return candidateApplicationResults;
	}


	public ApplicationDto getApplicationByJobPostIdAndCandidateId(long jobPostId, long candidateId) {
		if (!jobPostRepository.existsById(jobPostId)) {
			throw new CustomException("Job Post isn't exists");
		}

		Optional<Application> applicationEntity = applicationRepository.findApplicationByCanIdAndJobPostId(candidateId,
																										   jobPostId);
		if (applicationEntity.isEmpty()) {
			throw new CustomException("You have not applied for this job ");
		}


		return applicationMapper.applicationToApplicationDto(applicationEntity.get());
	}

	@Override
	public Long getApplicationAmount() {
		Long amount = applicationRepository.count();
		return amount;
	}

	@Override
	public long getAmountApplicationToEmployer(long employerId) {
		Optional<Employer> employerEntity = employerRepository.findById(employerId);
		if (employerEntity.isEmpty()) {
			throw new CustomException("Employer is not exists");
		}
		long amount = applicationRepository.getAmountApplicationToEmployer(employerId);
		return amount;
	}

	@Override
	public long getAmountApplicationByJobPostId(long jobPostId) {
		Optional<JobPost> jobPostEntity = jobPostRepository.findById(jobPostId);
		if (jobPostEntity.isEmpty()) {
			throw new CustomException("Job Post is not exists");
		}
		Long amount = applicationRepository.getAmountApplicationByJobPostId(jobPostId);

		return amount;
	}
}
