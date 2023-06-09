package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.entities.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CandidateMapper.class}, componentModel = "spring")
public interface ApplicationMapper {
	ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

	@Mapping(target = "jobPostId", source = "jobPost.id")
	@Mapping(target = "candidateId", source = "candidate.id")
	@Mapping(target = "candidateDTO", source = "candidate")
	ApplicationDto applicationToApplicationDto(Application application);
}
