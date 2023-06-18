package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WorkExperience {
	private Work[] works;
	@JsonProperty("total_work_experience")
	private Double totalWorkExperience;
	@JsonProperty("average_time")
	private Double averageTime;
}
