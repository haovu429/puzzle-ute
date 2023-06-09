package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.news.UpdateUserForAdminDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import hcmute.puzzle.infrastructure.dtos.request.PostCandidateRequest;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
	CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

	@Mapping(target = "userId", source = "user.id")
	CandidateDto candidateToCandidateDto(Candidate candidate);

	@Mapping(target = "userId", ignore = true)
	@Mapping(target = "id", ignore = true)
	CandidateDto postCandidateRequestToUserPostDto(PostCandidateRequest postCandidateRequest);

	@Mapping(target = "followingEmployers", ignore = true)
	@Mapping(target = "applicationEntities", ignore = true)
	@Mapping(target = "followingCompany", ignore = true)
	@Mapping(target = "experienceEntities", ignore = true)
	@Mapping(target = "evaluateEntities", ignore = true)
	@Mapping(target = "savedJobPost", ignore = true)
	@Mapping(target = "jobAlertEntities", ignore = true)
	@Mapping(target = "user", ignore = true)
	Candidate candidateDtoToCandidate(CandidateDto candidateDto);


	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "followingEmployers", ignore = true)
	@Mapping(target = "applicationEntities", ignore = true)
	@Mapping(target = "followingCompany", ignore = true)
	@Mapping(target = "experienceEntities", ignore = true)
	@Mapping(target = "evaluateEntities", ignore = true)
	@Mapping(target = "savedJobPost", ignore = true)
	@Mapping(target = "jobAlertEntities", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateCandidateFromDto(CandidateDto dto, @MappingTarget Candidate candidate);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "followingEmployers", ignore = true)
	@Mapping(target = "applicationEntities", ignore = true)
	@Mapping(target = "followingCompany", ignore = true)
	@Mapping(target = "experienceEntities", ignore = true)
	@Mapping(target = "evaluateEntities", ignore = true)
	@Mapping(target = "savedJobPost", ignore = true)
	@Mapping(target = "jobAlertEntities", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateCandidateFromPostCandidateRequest(PostCandidateRequest postCandidateRequest, @MappingTarget Candidate candidate);

	default Candidate candidateDtoToCandidateHandmade(CandidateDto candidateDto) {
		Candidate candidate = Candidate.builder()
									   .firstName(candidateDto.getFirstName())
									   .lastName(candidateDto.getLastName())
									   .emailContact(candidateDto.getEmailContact())
									   .phoneNum(candidateDto.getPhoneNum())
									   .introduction(candidateDto.getIntroduction())
									   .educationLevel(candidateDto.getEducationLevel())
									   .workStatus(candidateDto.getWorkStatus())
									   .blind(candidateDto.getBlind())
									   .deaf(candidateDto.getDeaf())
									   .communicationDis(candidateDto.getCommunicationDis())
									   .handDis(candidateDto.getHandDis())
									   .labor(candidateDto.getLabor())
									   .detailDis(candidateDto.getDetailDis())
									   .verifiedDis(candidateDto.getVerifiedDis())
									   .skills(candidateDto.getSkills())
									   .services(candidateDto.getServices())
									   .position(candidateDto.getPosition())
									   .build();
		return candidate;
	}
}
