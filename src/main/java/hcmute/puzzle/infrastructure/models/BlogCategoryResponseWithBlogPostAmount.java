package hcmute.puzzle.infrastructure.models;

import hcmute.puzzle.infrastructure.dtos.response.BlogCategoryDto;
import hcmute.puzzle.infrastructure.dtos.response.BlogPostResponse;
import hcmute.puzzle.infrastructure.entities.BlogCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BlogCategoryResponseWithBlogPostAmount {
	BlogCategoryDto blogCategory;
	Long blogPostAmount;
}
