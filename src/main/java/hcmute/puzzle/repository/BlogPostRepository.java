package hcmute.puzzle.repository;

import hcmute.puzzle.entities.BlogPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogPostRepository extends JpaRepository<BlogPostEntity, Long> {

  @Query("SELECT b FROM BlogPostEntity b WHERE b.id =:blog_post_id")
  BlogPostEntity getById(@Param("blog_post_id") long blogPostId);
}
