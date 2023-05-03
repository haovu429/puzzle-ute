package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {}
