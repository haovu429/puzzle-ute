package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Work {
	private String company;
	@JsonProperty("end_date")
	private String endDate;
	@JsonProperty("job_title")
	private String jobTitle;
	private String seniority;
	@JsonProperty("start_date")
	private String startDate;
	private String month;
}
