package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.news.UpdateUserForAdminDto;
import hcmute.puzzle.infrastructure.dtos.olds.CategoryDto;
import hcmute.puzzle.infrastructure.dtos.request.CreateCategoryRequest;
import hcmute.puzzle.infrastructure.entities.Category;
import hcmute.puzzle.infrastructure.entities.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

	CategoryDto categoryToCategoryDto(Category category);

	@Mapping(target = "id", ignore = true)
	Category categoryDtoToCategory(CategoryDto categoryDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateCategoryFromDto(CategoryDto categoryDto, @MappingTarget Category category);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "image", ignore = true)
	void updateCategoryFromCreateCategoryRequest(CreateCategoryRequest createCategoryRequest, @MappingTarget Category category);
}
