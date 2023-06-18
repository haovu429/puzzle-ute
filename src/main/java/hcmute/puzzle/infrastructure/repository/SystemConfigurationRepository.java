package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
	//	Optional<SystemConfiguration> findSystemConfigurationByKey(String key);

	Optional<SystemConfiguration> findByKey(String key);
}

