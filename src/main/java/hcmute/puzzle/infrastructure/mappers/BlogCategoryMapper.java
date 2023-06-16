package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.response.BlogCategoryDto;
import hcmute.puzzle.infrastructure.entities.BlogCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BlogCategoryMapper {
	BlogCategoryMapper INSTANCE = Mappers.getMapper(BlogCategoryMapper.class);

	BlogCategoryDto blogCategoryToBlogCategoryDto(BlogCategory blogCategory);
}
