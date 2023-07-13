package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HirizeIQData {
	private Long id;

	@JsonProperty("result")
	private HirizeIQResult result;

	@JsonProperty("request_parameters")
	private HirizeIQRequestParameters requestParameters;
}
