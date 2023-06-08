package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.JobPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {EmployerMapper.class, CompanyMapper.class}, componentModel = "spring")
public interface JobPostMapper {

	JobPostMapper INSTANCE = Mappers.getMapper(JobPostMapper.class);

	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "categoryName", source = "category.name")
	JobPostDto jobPostToJobPostDto(JobPost jobPost);
}
