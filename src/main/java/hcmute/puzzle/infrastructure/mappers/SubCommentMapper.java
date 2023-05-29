package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.SubCommentDto;
import hcmute.puzzle.infrastructure.entities.SubCommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
		componentModel = "spring"
)
public interface SubCommentMapper {

	SubCommentMapper INSTANCE = Mappers.getMapper(SubCommentMapper.class);

	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "userId", source = "author.id")
	@Mapping(target = "commentId", source = "comment.id")
	SubCommentDto subCommentToSubCommentDto(SubCommentEntity entity);

//	@Mapping(target = "username", source = "username")
//	@Mapping(target = "email", source = "email")
//	@Mapping(target = "phone", source = "phone")
//	@Mapping(target = "avatar", source = "avatar")
//	@Mapping(target = "fullName", source = "fullName")
//	@Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
//	UserPostDto mapToUserPostDto(Map<String, Object> map);
}
