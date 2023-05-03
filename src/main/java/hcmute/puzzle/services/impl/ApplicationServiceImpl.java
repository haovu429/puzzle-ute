package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.ApplicationEntity;
import hcmute.puzzle.infrastructure.entities.CandidateEntity;
import hcmute.puzzle.infrastructure.entities.EmployerEntity;
import hcmute.puzzle.infrastructure.entities.JobPostEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.ApplicationRepository;
import hcmute.puzzle.infrastructure.repository.CandidateRepository;
import hcmute.puzzle.infrastructure.repository.EmployerRepository;
import hcmute.puzzle.infrastructure.repository.JobPostRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {
  @Autowired ApplicationRepository applicationRepository;

  @Autowired CandidateRepository candidateRepository;

  @Autowired JobPostRepository jobPostRepository;
  @Autowired Converter converter;

  @Autowired EmployerRepository employerRepository;

  @PersistenceContext public EntityManager em;

  @Override
  public ResponseObject findById(Long id) {
    Optional<ApplicationEntity> dto = applicationRepository.findById(id);
    if (!dto.isPresent()) {
      throw new CustomException("Can not found Address with id " + id);
    }
    return new ResponseObject(converter.toDTO(dto.get()));
  }

  @Override
  public ResponseObject deleteById(Long id) {
    Optional<ApplicationEntity> foundEntity = applicationRepository.findById(id);
    if (!foundEntity.isPresent()) {
      throw new RuntimeException("Can not found Application with id " + id);
    }
    applicationRepository.delete(foundEntity.get());
    return new ResponseObject(200, " successful delete application with id " + id, null);
  }

  @Override
  public ResponseObject findAll(Pageable pageable) {

    Page<ApplicationEntity> entities = applicationRepository.findAll(pageable);
    Page<ApplicationDto> dtos =
        entities.map(
            new Function<ApplicationEntity, ApplicationDto>() {
              @Override
              public ApplicationDto apply(ApplicationEntity entity) {
                return converter.toDTO(entity);
              }
            });
    return new ResponseObject(dtos);
  }

  @Override
  // Candidate apply jobPost
  public ResponseObject applyJobPost(long candidateId, long jobPostId) {
    Optional<CandidateEntity> candidate = candidateRepository.findById(candidateId);
    Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
    ApplicationEntity application = new ApplicationEntity();

    if (candidate.isEmpty()) {
      throw new NoSuchElementException("Candidate no value present");
    }

    if (jobPost.isEmpty()) {
      throw new NoSuchElementException("Employer no value present");
    }

    application.setCandidateEntity(candidate.get());
    application.setJobPostEntity(jobPost.get());
    applicationRepository.save(application);

    return new ResponseObject(200, " Apply success! ", converter.toDTO(application));
  }

  @Override
  public ResponseObject responseApplication(long applicationId, boolean isAccept, String note) {
    Optional<ApplicationEntity> application = applicationRepository.findById(applicationId);
    if (isAccept) {
      application.get().setResult("ACCEPT");
    } else {
      application.get().setResult("REJECT");
    }
    application.get().setNote(note);
    applicationRepository.save(application.get());
    return new ResponseObject(200, "Response success", null);
  }

  @Override
  public ResponseObject responseApplicationByCandidateAndJobPost(
      long candidateId, long joPostId, boolean isAccept, String note) {
    Optional<ApplicationEntity> application =
        applicationRepository.findApplicationByCanIdAndJobPostId(candidateId, joPostId);
    if (isAccept) {
      application.get().setResult("ACCEPT");
    } else {
      application.get().setResult("REJECT");
    }
    application.get().setNote(note);
    applicationRepository.save(application.get());
    return new ResponseObject(200, "Response success", null);
  }

  public ResponseObject getApplicationByJobPostId(long jobPostId) {
    if (!jobPostRepository.existsById(jobPostId)) {
      throw new CustomException("Job Post isn't exists");
    }

    List<ApplicationDto> applicationDTOS =
        applicationRepository.findApplicationByJobPostId(jobPostId).stream()
            .map(
                application -> {
                  ApplicationDto applicationDTO = converter.toDTO(application);
                  Optional<CandidateEntity> candidate =
                      candidateRepository.findById(applicationDTO.getCandidateId());
                  if (candidate.isEmpty()) {
                    throw new CustomException("Data candidate is wrong");
                  }

                  applicationDTO.setCandidateDTO(converter.toDTO(candidate.get()));
                  return applicationDTO;
                })
            .collect(Collectors.toList());
    return new ResponseObject(
        200, "List application for job post id = " + jobPostId, applicationDTOS);
  }

  public DataResponse getCandidateAppliedToJobPostIdAndResult(long jobPostId) {
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
        Optional<JobPostEntity> jobPost = jobPostRepository.findById(jobPostId);
        if (jobPost.isEmpty()) {
          throw new CustomException("Job Post isn't exists");
        }
        String position = jobPost.get().getTitle();

        List<Map<String, Object>> response =
            applicationRepository.findApplicationByJobPostId(jobPostId).stream()
                .map(
                    application -> {
                      CandidateEntity candidate = application.getCandidateEntity();
                      Map<String, Object> candidateAndResult = new HashMap<>();
                      candidateAndResult.put("position", position);
                      candidateAndResult.put("candidate", converter.toDTO(candidate));
                      candidateAndResult.put("application", converter.toDTO(application));
                      return candidateAndResult;
                    })
                .collect(Collectors.toList());
    return new DataResponse(response);
  }

  public DataResponse getCandidateAppliedToEmployerAndResult(long employerId) {
    List<Map<String, Object>> response =
            applicationRepository.findApplicationByEmployerId(employerId).stream()
                    .map(
                            application -> {
                              CandidateEntity candidate = application.getCandidateEntity();
                              Map<String, Object> candidateAndResult = new HashMap<>();
                              candidateAndResult.put("position", application.getJobPostEntity().getTitle());
                              candidateAndResult.put("candidate", converter.toDTO(candidate));
                              candidateAndResult.put("application", converter.toDTO(application));
                              return candidateAndResult;
                            })
                    .collect(Collectors.toList());
    return new DataResponse(response);
  }


  public ResponseObject getApplicationByJobPostIdAndCandidateId(long jobPostId, long candidateId) {
    if (!jobPostRepository.existsById(jobPostId)) {
      throw new CustomException("Job Post isn't exists");
    }

    Optional<ApplicationEntity> applicationEntity =
        applicationRepository.findApplicationByCanIdAndJobPostId(candidateId, jobPostId);
    if (applicationEntity.isEmpty()) {
      throw new CustomException("You have not applied for this job ");
    }

    return new ResponseObject(
        200,
        "Application for job post id = " + jobPostId,
        converter.toDTO(applicationEntity.get()));
  }

  @Override
  public ResponseObject getApplicationAmount() {
    long amount = applicationRepository.count();

    return new ResponseObject(200, "Application amount", amount);
  }

  @Override
  public DataResponse getAmountApplicationToEmployer(long employerId) {
    Optional<EmployerEntity> employerEntity = employerRepository.findById(employerId);
    if (employerEntity.isEmpty()) {
      throw new CustomException("Employer is not exists");
    }
    return new DataResponse(applicationRepository.getAmountApplicationToEmployer(employerId));
  }

  @Override
  public DataResponse getAmountApplicationByJobPostId(long jobPostId) {
    Optional<JobPostEntity> jobPostEntity = jobPostRepository.findById(jobPostId);
    if (jobPostEntity.isEmpty()) {
      throw new CustomException("Job Post is not exists");
    }

    return new DataResponse(applicationRepository.getAmountApplicationByJobPostId(jobPostId));
  }
}
