package hcmute.puzzle.infrastructure.dtos.request;

import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import lombok.Data;

import java.util.Date;
@Data
public class JobPostUserPostRequest {
	private String title;
	private String position;
	private String employmentType;
	private String workplaceType;
	private String description;
	private String city;
	private String address;
	private String educationLevel;
	private Integer experienceYear;
	private Integer quantity;
	private Long minBudget;
	private Long maxBudget;
	private Date deadline;
	private String workStatus;
	private Boolean blind;
	private Boolean deaf;
	private Boolean communicationDis;
	private Boolean handDis;
	private Boolean labor;
	private String skills;
	private Boolean isPublic;
	private Boolean canApply;
	private Long companyId;
	private Long categoryId;
}
