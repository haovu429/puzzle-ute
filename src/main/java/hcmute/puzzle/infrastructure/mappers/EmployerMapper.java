package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.entities.JobPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployerMapper {
	JobPostMapper INSTANCE = Mappers.getMapper(JobPostMapper.class);

	@Mapping(target = "userId", source = "user.id")
	EmployerDto EmployerToEmployerDto(Employer employer);
}
