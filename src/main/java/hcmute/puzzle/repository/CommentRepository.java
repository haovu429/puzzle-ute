package hcmute.puzzle.repository;

import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
