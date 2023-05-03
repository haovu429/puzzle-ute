package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
