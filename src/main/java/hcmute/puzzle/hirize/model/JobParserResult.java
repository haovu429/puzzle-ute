package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JobParserResult {
	private String[] skills;
	private String description;
	private String city;
	private String country;
	private String seniority;
	@JsonProperty("job_title")
	private String jobTitle;
}
