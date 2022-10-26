package hcmute.puzzle.repository;

import hcmute.puzzle.entities.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
}
