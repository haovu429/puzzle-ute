package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.entities.CommentEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
		uses = {SubCommentMapper.class},
		componentModel = "spring"
)
public interface CommentMapper {

	CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "userId", source = "author.id")
	@Mapping(target = "blogPostId", source = "blogPostEntity.id")
	CommentDto commentToCommentDto(CommentEntity entity);

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
