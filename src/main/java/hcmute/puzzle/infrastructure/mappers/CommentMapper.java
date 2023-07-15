package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.news.CreateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.request.UpdateCommentRequest;
import hcmute.puzzle.infrastructure.entities.Comment;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
		uses = {SubCommentMapper.class},
		componentModel = "spring"
)
public interface CommentMapper {

	CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "userId", source = "author.id")
	@Mapping(target = "blogPostId", source = "blogPost.id")
	@Mapping(target = "canEdit", ignore = true)
//	@Mapping(target = "nickname", ignore = true)
	@Mapping(target = "avatar", ignore = true)
	CommentDto commentToCommentDto(Comment entity);

	List<CommentDto> commentListToCommentDtoList(List<Comment> comments);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "likeNum", ignore = true)
	@Mapping(target = "disLikeNum", ignore = true)
	@Mapping(target = "blogPost", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "subComments", ignore = true)
	Comment createCommentRequestToComment(CreateCommentRequest createCommentRequest);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "likeNum", ignore = true)
	@Mapping(target = "disLikeNum", ignore = true)
	@Mapping(target = "blogPost", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "subComments", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "nickname", ignore = true)
	@Mapping(target = "email", ignore = true)
	void updateCommentFromUpdateCommentRequest(UpdateCommentRequest updateCommentRequest,@MappingTarget Comment comment);
//	default CommentDto commentToCommentDto(CommentEntity entity) {
//
//		CommentDto commentDto = CommentDto.builder()
//				.id(com)
//				.build();
//	}

//	@Mapping(target = "username", source = "username")
//	@Mapping(target = "email", source = "email")
//	@Mapping(target = "phone", source = "phone")
//	@Mapping(target = "avatar", source = "avatar")
//	@Mapping(target = "fullName", source = "fullName")
//	@Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
//	UserPostDto mapToUserPostDto(Map<String, Object> map);
}
