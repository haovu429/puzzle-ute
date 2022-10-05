package hcmute.puzzle.repository;

import hcmute.puzzle.entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findAllById(Long billId);

    Optional<ApplicationEntity> findById(Long id);
}
