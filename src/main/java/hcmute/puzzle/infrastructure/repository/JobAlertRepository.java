package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.JobAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {
  List<JobAlert> findAllByCandidate_Id(long candidateId);
}
