package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostUpdateRequest;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
		uses = {CommentMapper.class},
		componentModel = "spring"
)
public interface BlogPostMapper {
	BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

//	@Mapping(target = "username", source = "username")
//	@Mapping(target = "email", source = "email")
//	@Mapping(target = "phone", source = "phone")
//	@Mapping(target = "avatar", source = "avatar")
//	@Mapping(target = "fullName", source = "fullName")
//	@Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
//	default BlogPostDto blogPostToBlogPostDto(BlogPostEntity entity){
//		BlogPostDto blogPostDto= BlogPostDto.builder()
//				.id(entity.getId())
//				.title(entity.getTitle())
//				.thumbnail(entity.getThumbnail())
//				.tags(entity.getTags())
//				.body(entity.getBody())
//				.build();
//		return blogPostDto;
//	}


	@Mapping(target = "blogCategoryId", source = "blogCategory.id")
	@Mapping(target = "userId", source = "author.id")
	@Mapping(target = "comments", ignore = true)
	BlogPostDto blogPostToBlogPostDto(BlogPost entity);

//	default BlogPostEntity blogPostDtoToBlogPost(BlogPostDto dto){
//		BlogPostEntity blogPost= BlogPostEntity.builder()
//				.id(dto.getId())
//				.title(dto.getTitle())
//				.thumbnail(dto.getThumbnail())
//				.tags(dto.getTags())
//				.body(dto.getBody())
//				.build();
//		return blogPost;
//	}


	@Mapping(target = "blogCategory", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "isActive", ignore = true)
	BlogPost blogPostRequestToBlogPost(BlogPostDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "blogCategory", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "thumbnail", ignore = true)
	@Mapping(target = "isActive", ignore = true)
	void updateBlogPostFromBlogPostRequestWithoutThumbnail(BlogPostUpdateRequest blogPostUpdateRequest, @MappingTarget BlogPost entity);

}
