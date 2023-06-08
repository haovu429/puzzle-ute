package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.ExtraInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExtraInfoRepository extends JpaRepository<ExtraInfo, Long> {
    Set<ExtraInfo> getExtraInfoEntitiesByType(String type);
}
