package hcmute.puzzle.infrastructure.models.cohere;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummarizeRequest {
	private String text;
	private String length;
	private String format;
	private String model;
	@JsonProperty("additional_command")
	private String additionalCommand;
	private Float temperature;
}
