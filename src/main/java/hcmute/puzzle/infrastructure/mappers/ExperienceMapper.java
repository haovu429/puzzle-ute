package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.EvaluateDto;
import hcmute.puzzle.infrastructure.dtos.olds.ExperienceDto;
import hcmute.puzzle.infrastructure.entities.Evaluate;
import hcmute.puzzle.infrastructure.entities.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

	ExperienceMapper INSTANCE = Mappers.getMapper(ExperienceMapper.class);
	@Mapping(target = "candidateId", source = "candidate.id")
	ExperienceDto evaluateToEvaluateDto(Experience experience);
}
