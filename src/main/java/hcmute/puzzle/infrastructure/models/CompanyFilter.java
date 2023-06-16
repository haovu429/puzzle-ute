package hcmute.puzzle.infrastructure.models;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyFilter {
	String searchKey;
	Boolean isPublic;
	Boolean isActive;
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
