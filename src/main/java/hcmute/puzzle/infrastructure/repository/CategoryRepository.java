package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
