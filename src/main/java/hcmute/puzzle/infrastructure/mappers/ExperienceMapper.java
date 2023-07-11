package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.ExperienceDto;
import hcmute.puzzle.infrastructure.dtos.request.CreateExperienceRequest;
import hcmute.puzzle.infrastructure.dtos.request.UpdateExperienceRequest;
import hcmute.puzzle.infrastructure.entities.Experience;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

	ExperienceMapper INSTANCE = Mappers.getMapper(ExperienceMapper.class);

	@Mapping(target = "candidateId", source = "candidate.id")
	ExperienceDto experienceToExperienceDto(Experience experience);

	@Mapping(target = "candidate", ignore = true)
	Experience experienceDtoToExperience(ExperienceDto experienceDto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "candidate", ignore = true)
	Experience createExperienceRequestToExperience(CreateExperienceRequest createExperienceRequest);

	UpdateExperienceRequest experienceDtoToUpdateExperienceRequest(ExperienceDto experienceDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "candidate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateExperienceFromUpdateExperienceRequest(UpdateExperienceRequest updateExperienceRequest,
			@MappingTarget Experience experience);
}
