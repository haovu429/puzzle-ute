package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByBlogPostIdOrderByCreatedAtDesc(long blogPost_id);
}
