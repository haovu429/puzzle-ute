package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobParserRequest {

	@JsonProperty("job_description")
	private String description;
}
