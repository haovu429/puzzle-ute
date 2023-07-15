package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.news.CreateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.news.CreateSubCommentRequest;
import hcmute.puzzle.infrastructure.dtos.olds.SubCommentDto;
import hcmute.puzzle.infrastructure.dtos.request.UpdateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.request.UpdateSubCommentRequest;
import hcmute.puzzle.infrastructure.entities.Comment;
import hcmute.puzzle.infrastructure.entities.SubComment;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
		componentModel = "spring"
)
public interface SubCommentMapper {

	SubCommentMapper INSTANCE = Mappers.getMapper(SubCommentMapper.class);

	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "userId", source = "author.id")
	@Mapping(target = "commentId", source = "comment.id")
	@Mapping(target = "canEdit", ignore = true)
	@Mapping(target = "avatar", ignore = true)
	SubCommentDto subCommentToSubCommentDto(SubComment entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "comment", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "interact", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	SubComment createCommentRequestToComment(CreateSubCommentRequest createSubCommentRequest);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "comment", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "interact", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "nickname", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	void updateCommentFromUpdateSubCommentRequest(UpdateSubCommentRequest updateSubCommentRequest,@MappingTarget SubComment subComment);

	List<SubCommentDto> subCommentListToSubCommentDtoList(List<SubComment> employees);
//	@Mapping(target = "username", source = "username")
//	@Mapping(target = "email", source = "email")
//	@Mapping(target = "phone", source = "phone")
//	@Mapping(target = "avatar", source = "avatar")
//	@Mapping(target = "fullName", source = "fullName")
//	@Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
//	UserPostDto mapToUserPostDto(Map<String, Object> map);
}
