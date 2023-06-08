package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {}
