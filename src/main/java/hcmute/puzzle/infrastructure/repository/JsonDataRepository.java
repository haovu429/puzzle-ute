package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.infrastructure.entities.JsonData;
import hcmute.puzzle.infrastructure.models.enums.JsonDataType;
import io.swagger.v3.core.util.Json;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JsonDataRepository extends JpaRepository<JsonData, Long> {

	List<JsonData> findAllByApplicationIdAndType(Long applicationId, JsonDataType type);

	List<JsonData> findByCreatedByAndType(String createdBy, JsonDataType type);
}
