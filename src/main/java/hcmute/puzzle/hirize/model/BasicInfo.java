package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BasicInfo {
	private String name;
	private String[] links;
	@JsonProperty("current_position")
	private String currentPosition;
}
