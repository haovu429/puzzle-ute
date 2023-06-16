package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

  Set<Experience> findAllByCandidate_Id(long candidateId);
}
