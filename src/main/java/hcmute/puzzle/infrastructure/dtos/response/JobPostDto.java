package hcmute.puzzle.infrastructure.dtos.response;

import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.entities.Company;
import hcmute.puzzle.infrastructure.entities.Employer;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
public class JobPostDto {
	private long id;
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
	private Date expiryDate;
	private String workStatus;
	private Boolean blind;
	private Boolean deaf;
	private Boolean communicationDis;
	private Boolean handDis;
	private Boolean labor;
	private String skills;
	private Long views;
	private Boolean canApply;
	private EmployerDto createdEmployer;
	private CompanyResponse company;
	private String categoryName;
	private Date createdAt;
	private Date updatedAt;
}
