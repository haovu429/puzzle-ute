package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

  @Query("SELECT b FROM BlogPost b WHERE b.id =:blog_post_id")
  BlogPost getById(@Param("blog_post_id") long blogPostId);

  List<BlogPost> findAllByAuthorId(long author_id);
}
