package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
}
