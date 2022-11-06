package hcmute.puzzle.repository;

import hcmute.puzzle.entities.ExtraInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExtraInfoRepository extends JpaRepository<ExtraInfoEntity, Long> {
    Set<ExtraInfoEntity> getExtraInfoEntitiesByType(String type);
}
