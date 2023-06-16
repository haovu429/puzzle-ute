package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
