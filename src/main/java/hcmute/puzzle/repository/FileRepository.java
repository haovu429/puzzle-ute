package hcmute.puzzle.repository;

import hcmute.puzzle.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {}
