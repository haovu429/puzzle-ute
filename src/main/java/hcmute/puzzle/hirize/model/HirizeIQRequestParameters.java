package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HirizeIQRequestParameters {
	@JsonProperty("job_description")
	private String jobDescription;

	@JsonProperty("parser")
	private ParserResult parser;
}
