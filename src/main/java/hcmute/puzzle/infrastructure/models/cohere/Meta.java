package hcmute.puzzle.infrastructure.models.cohere;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Meta {
	@JsonProperty("api_version")
	private ApiVersion apiVersion;
}
