package hcmute.puzzle.repository;

import hcmute.puzzle.entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
  // Set<ApplicationEntity> findAllById(Long billId);

  // Optional<ApplicationEntity> findById(Long id);
  @Query(
      "SELECT a FROM ApplicationEntity a WHERE a.candidateEntity.id = :candidateId AND a.jobPostEntity.id = :jobPostId")
  Set<ApplicationEntity> findApplicationByCanIdAndJobPostId(
      @Param("candidateId") long candidateId, @Param("jobPostId") long jobPostId);

  @Query(
          "SELECT a FROM ApplicationEntity a WHERE a.jobPostEntity.id = :jobPostId")
  Set<ApplicationEntity> findApplicationByJobPostId(@Param("jobPostId") long jobPostId);

}
