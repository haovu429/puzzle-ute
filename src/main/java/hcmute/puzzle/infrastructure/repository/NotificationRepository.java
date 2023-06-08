package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {}
