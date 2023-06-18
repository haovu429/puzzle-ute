package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HirizeResponse<T> {
	Boolean success;

	@JsonProperty("remaining_credit")
	Double remainingCredit;

	@JsonProperty("data")
	T data;
}
