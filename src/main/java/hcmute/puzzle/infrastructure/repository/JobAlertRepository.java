package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.JobAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface JobAlertRepository extends JpaRepository<JobAlertEntity, Long> {
  Set<JobAlertEntity> findAllByCandidateEntity_Id(long candidateId);
}
