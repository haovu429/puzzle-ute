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
	Boolean isAscSort = true;
	String sortColumn;
	Date createdAtFrom;
	Date createdAtTo = new Date();
	Date updatedAtFrom;
	Date updatedAtTo = new Date();
}
