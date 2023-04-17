package hcmute.puzzle.repository;

import hcmute.puzzle.entities.FileTypeEntity;
import hcmute.puzzle.model.enums.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileTypeRepository extends JpaRepository<FileTypeEntity, Long> {

    Optional<FileTypeEntity> findByCategory(FileCategory category);
}
