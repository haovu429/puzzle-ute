package hcmute.puzzle.infrastructure.dtos.request;

import hcmute.puzzle.infrastructure.models.enums.JsonDataType;
import lombok.Data;

@Data
public class JsonDataUpdateRequest {
	private JsonDataType type;
	private String data;
}
