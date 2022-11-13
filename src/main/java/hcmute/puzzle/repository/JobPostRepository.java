package hcmute.puzzle.repository;

import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface JobPostRepository extends JpaRepository<JobPostEntity, Long> {

  @Query(
      "SELECT c FROM CandidateEntity c WHERE c.id in (SELECT a.candidateEntity.id FROM ApplicationEntity a WHERE a.jobPostEntity.id = :jobPostId)")
  Set<CandidateEntity> getCandidateApplyJobPost(@Param("jobPostId") long jobPostId);

  @Query(
          "SELECT jp FROM JobPostEntity jp WHERE jp.id in (SELECT a.jobPostEntity.id FROM ApplicationEntity a WHERE a.candidateEntity.id = :candidateId)")
  Set<JobPostEntity> getJobPostCandidateApplied(@Param("candidateId") long candidateId);

  @Query(
      "SELECT jp FROM JobPostEntity jp, ApplicationEntity ap, CandidateEntity can WHERE can.id = :candidateId AND ap.candidateEntity.id = can.id AND ap.jobPostEntity.id = jp.id")
  Set<JobPostEntity> findAllByAppliedCandidateId(@Param("candidateId") long candidateId);

  @Query(
          "SELECT jp FROM JobPostEntity jp WHERE jp.createdEmployer.id = :employerId")
  Set<JobPostEntity> findAllByCreatedEmployerId(@Param("employerId") long employerId);

//  @Query(
//          "SELECT jp FROM JobPostEntity jp, CandidateEntity can WHERE can.id = :candidateId AND jp.id in can.savedJobPost")
//  Set<JobPostEntity> findAllBySavedCandidateId(@Param("candidateId") long candidateId);
}
