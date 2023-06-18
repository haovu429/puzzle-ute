package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIMatcherRequest {
	@JsonProperty("job_title")
	private String jobTitle;
	private String seniority;
	private String[] skills;
	private String payload;
	@JsonProperty("file_name")
	private String fileName;
}
