package hcmute.puzzle.infrastructure.models;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
	Boolean isPublic;
	Boolean isActive;
	Integer numDayAgo;
	@Builder.Default
	Boolean isAscSort = true;
	String sortColumn;
	Date createdAtFrom;
	@Builder.Default
	Date createdAtTo = new Date();
	Date updatedAtFrom;
	@Builder.Default
	Date updatedAtTo = new Date();
}
