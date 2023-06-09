package hcmute.puzzle.infrastructure.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JobPostFilterRequest implements Serializable {
	Long minBudget;
	Long maxBudget;
	Integer experienceYear;
	List<String> searchKeys;
	List<String> employmentTypes;
	String city;
	String position;
	List<String> skills;
	List<Long> categoryIds;
	Boolean canApply;
	Boolean isActive;
	Integer numDayAgo;
	Boolean isAscSort = true;
	String sortColumn;
	Date createdAtFrom;
	Date createdAtTo;
	Date updatedAtFrom;
	Date updatedAtTo;
}
