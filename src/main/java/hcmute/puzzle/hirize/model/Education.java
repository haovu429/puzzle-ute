package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Education {
	private String major;
	@JsonProperty("school_name")
	private String schoolName;
	@JsonProperty("education_level")
	private String educationLevel;
	@JsonProperty("graduation_year")
	private String graduationYear;
}
