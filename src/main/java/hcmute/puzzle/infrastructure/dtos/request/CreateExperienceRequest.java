package hcmute.puzzle.infrastructure.dtos.request;

import lombok.Data;

import java.util.Date;

@Data
public class CreateExperienceRequest {
	private String title;
	private String employmentType;
	private String company;
	private Boolean isWorking;
	private String industry;
	private Date startDate;
	private Date endDate;
	private String description;
	private String skills;
}
