package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiMatcherRequestParameters {
	@JsonProperty("parser_result")
	private ParserResult parserResult;
	private String[] skills;
	private String seniority;
	@JsonProperty("job_title")
	private String jobTitle;
}
