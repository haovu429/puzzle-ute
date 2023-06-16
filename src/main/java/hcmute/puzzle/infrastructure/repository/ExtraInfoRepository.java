package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.ExtraInfo;
import hcmute.puzzle.infrastructure.models.enums.ExtraInfoType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ExtraInfoRepository extends JpaRepository<ExtraInfo, Long> {
    List<ExtraInfo> getExtraInfoEntitiesByType(ExtraInfoType type);
}
