package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {}
