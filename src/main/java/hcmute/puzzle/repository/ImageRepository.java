package hcmute.puzzle.repository;

import hcmute.puzzle.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {}
