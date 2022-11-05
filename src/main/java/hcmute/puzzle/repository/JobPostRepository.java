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
}
