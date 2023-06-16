package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.entities.Employer;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface EmployerMapper {
	JobPostMapper INSTANCE = Mappers.getMapper(JobPostMapper.class);

	@Mapping(target = "userId", source = "user.id")
	EmployerDto employerToEmployerDto(Employer employer);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "followCandidates", ignore = true)
	@Mapping(target = "jobPostEntities", ignore = true)
	@Mapping(target = "evaluateEntities", ignore = true)
	@Mapping(target = "companyEntities", ignore = true)
	@Mapping(target = "user", ignore = true)
	Employer employerDtoToEmployer(EmployerDto employer);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "followCandidates", ignore = true)
	@Mapping(target = "jobPostEntities", ignore = true)
	@Mapping(target = "evaluateEntities", ignore = true)
	@Mapping(target = "companyEntities", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateEmployerFromEmployerDto(EmployerDto employerDto,@MappingTarget Employer employer);
}
