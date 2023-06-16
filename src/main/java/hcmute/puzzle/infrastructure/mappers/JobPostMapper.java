package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.dtos.request.JobPostAdminPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.JobPostUserPostRequest;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.ExtraInfo;
import hcmute.puzzle.infrastructure.entities.JobPost;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {EmployerMapper.class, CompanyMapper.class}, componentModel = "spring")
public interface JobPostMapper {

	JobPostMapper INSTANCE = Mappers.getMapper(JobPostMapper.class);

	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "categoryName", source = "category.name")
	JobPostDto jobPostToJobPostDto(JobPost jobPost);

	@Mapping(target = "views", ignore = true)
	@Mapping(target = "isActive", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	@Mapping(target = "applications", ignore = true)
	@Mapping(target = "viewedUsers", ignore = true)
	@Mapping(target = "createdEmployer", ignore = true)
	@Mapping(target = "savedCandidates", ignore = true)
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "category", ignore = true)
	JobPost jobPostUserPostRequestToJobPost(JobPostUserPostRequest jobPostUserPostRequest);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "isActive", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	@Mapping(target = "applications", ignore = true)
	@Mapping(target = "viewedUsers", ignore = true)
	@Mapping(target = "createdEmployer", ignore = true)
	@Mapping(target = "savedCandidates", ignore = true)
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "category", ignore = true)
	void updateJobPostFromJobPostUserPostRequest(JobPostUserPostRequest jobPostUserPostRequest, @MappingTarget JobPost jobPost);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "applications", ignore = true)
	@Mapping(target = "viewedUsers", ignore = true)
	@Mapping(target = "createdEmployer", ignore = true)
	@Mapping(target = "savedCandidates", ignore = true)
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "category", ignore = true)
	void updateJobPostFromJobPostAdminPostRequest(JobPostAdminPostRequest jobPostAdminPostRequest, @MappingTarget JobPost jobPost);
}