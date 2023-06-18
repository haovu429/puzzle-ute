package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HirizeIQRequest {
	private String payload;
	@JsonProperty("file_name")
	private String fileName;
	@JsonProperty("job_description")
	private String jobDescription;
}
