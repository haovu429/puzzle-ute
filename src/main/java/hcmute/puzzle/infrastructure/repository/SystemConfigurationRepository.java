package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.BlogCategory;
import hcmute.puzzle.infrastructure.entities.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
}
