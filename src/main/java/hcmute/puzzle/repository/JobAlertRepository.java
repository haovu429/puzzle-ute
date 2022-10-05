package hcmute.puzzle.repository;

import hcmute.puzzle.entities.JobAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobAlertRepository extends JpaRepository<JobAlertEntity, Long> {
}
