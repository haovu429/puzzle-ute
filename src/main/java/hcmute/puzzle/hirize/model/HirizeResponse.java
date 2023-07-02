package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class HirizeResponse<T> implements Serializable {
	Boolean success;

	@JsonProperty("remaining_credit")
	Double remainingCredit;

	@JsonProperty("data")
	T data;
}
