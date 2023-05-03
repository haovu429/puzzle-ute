package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
