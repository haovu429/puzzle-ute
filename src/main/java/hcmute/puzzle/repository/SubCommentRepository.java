package hcmute.puzzle.repository;

import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.SubCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCommentRepository extends JpaRepository<SubCommentEntity, Long> {
}
