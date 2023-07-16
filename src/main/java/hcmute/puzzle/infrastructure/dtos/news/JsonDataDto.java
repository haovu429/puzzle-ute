package hcmute.puzzle.infrastructure.dtos.news;

import hcmute.puzzle.infrastructure.models.enums.JsonDataType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class JsonDataDto {
	private long id;
	private long hirizeId;
	private Long applicationId;
	private JsonDataType type;
	private String data;
}
