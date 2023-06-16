package hcmute.puzzle.infrastructure.dtos.response;

import lombok.Data;

@Data
public class BlogCategoryDto {
	private long id;
	private String name;
	private Boolean isActive;
}
