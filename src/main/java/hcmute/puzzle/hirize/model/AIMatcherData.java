package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AIMatcherData {
	private String id;

	@JsonProperty("matcher_result")
	private MatcherResult matcherResult;

	@JsonProperty("request_parameters")
	private AiMatcherRequestParameters requestParameters;

}
