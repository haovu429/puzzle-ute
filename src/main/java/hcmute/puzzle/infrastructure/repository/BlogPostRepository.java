package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor {

  @Query("SELECT b FROM BlogPost b WHERE b.id =:blogPostId")
  BlogPost getById(@Param("blogPostId") long blogPostId);

  List<BlogPost> findAllByAuthorId(long author_id);
}
