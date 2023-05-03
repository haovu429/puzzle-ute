package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.SubCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCommentRepository extends JpaRepository<SubCommentEntity, Long> {
}
