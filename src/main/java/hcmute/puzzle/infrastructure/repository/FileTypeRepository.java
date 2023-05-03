package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.FileTypeEntity;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileTypeRepository extends JpaRepository<FileTypeEntity, Long> {

    Optional<FileTypeEntity> findByCategory(FileCategory category);
}
