package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.SubComment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
	List<SubComment> findAllByCommentId(long comment_id);

	List<SubComment> findAllByCommentId(long comment_id, Sort sort);

	void deleteByCommentId(long comment_id);
}
