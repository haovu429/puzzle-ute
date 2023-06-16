package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {

	@Query(value = "SELECT bc, COUNT(bp) FROM BlogCategory bc JOIN BlogPost bp ON bc.id = bp.blogCategory.id GROUP BY bc ORDER BY COUNT(bp)")
	public List<Object[]> getBlogCategoriesWithAmountBlogPost();
}
