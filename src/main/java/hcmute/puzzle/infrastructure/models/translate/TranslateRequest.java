package hcmute.puzzle.infrastructure.models.translate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranslateRequest {
	private TranslateObject[] data;
	private String from;
	private String to;
}
