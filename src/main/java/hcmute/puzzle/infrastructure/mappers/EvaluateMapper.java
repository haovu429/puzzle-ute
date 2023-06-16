package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.EmployerDto;
import hcmute.puzzle.infrastructure.dtos.olds.EvaluateDto;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.entities.Evaluate;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface EvaluateMapper {
	EvaluateMapper INSTANCE = Mappers.getMapper(EvaluateMapper.class);

	@Mapping(target = "employerId", source = "employer.id")
	@Mapping(target = "candidateId", source = "candidate.id")
	EvaluateDto evaluateToEvaluateDto(Evaluate evaluate);
}
