package hcmute.puzzle.repository;

import hcmute.puzzle.entities.JobAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface JobAlertRepository extends JpaRepository<JobAlertEntity, Long> {
    Set<JobAlertEntity> findAllByCandidateEntity_Id(long candidateId);
}
