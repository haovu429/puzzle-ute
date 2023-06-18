package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MyAiMatcherRequest implements Serializable {
	@JsonProperty("job_title")
	private String jobTitle;
	private String seniority;
	private String[] skills;
	@JsonProperty("file_name")
	private String fileName;
}
