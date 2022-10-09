package hcmute.puzzle.repository;

import hcmute.puzzle.entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
  // Set<ApplicationEntity> findAllById(Long billId);

  // Optional<ApplicationEntity> findById(Long id);
}
