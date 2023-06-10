package hcmute.puzzle.infrastructure.dtos.request;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class BlogPostFilterRequest {
	Long categoryId;
	String searchKey;
	@Builder.Default
	Boolean isAscSort = true;
	String sortColumn;
	Date createdAtFrom;
	Date createdAtTo;
	Date updatedAtFrom;
	Date updatedAtTo;
	Boolean isPublic;
	Boolean isActive;
}
