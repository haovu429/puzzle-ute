package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.ExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExperienceRepository extends JpaRepository<ExperienceEntity, Long> {

  Set<ExperienceEntity> findAllByCandidateEntity_Id(long candidateId);
}
