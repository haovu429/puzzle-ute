package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {
}
