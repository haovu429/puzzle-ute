package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MyHirizeIQRequest implements Serializable {

	@JsonProperty("job_description")
	private String jobDescription;

//	@JsonProperty("job_description")
//	public void setJobDescription(String jobDescription) {
//		this.jobDescription = jobDescription;
//	}
//
//	@JsonProperty("job_description")
//	public String getJobDescription() {
//		return jobDescription;
//	}
}
