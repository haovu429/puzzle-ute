package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.JobAlertDto;
import hcmute.puzzle.infrastructure.entities.JobAlert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface JobAlertMapper {
	JobAlertMapper INSTANCE = Mappers.getMapper(JobAlertMapper.class);

	@Mapping(target = "candidateId", source = "candidate.id")
	JobAlertDto jobAlertToJobAlertDto(JobAlert jobAlert);
}
