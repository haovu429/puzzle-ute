package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.request.JobPostAdminPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.JobPostUserPostRequest;
import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import hcmute.puzzle.infrastructure.entities.JobPost;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {EmployerMapper.class, CompanyMapper.class}, componentModel = "spring")
public interface JobPostMapper {

	JobPostMapper INSTANCE = Mappers.getMapper(JobPostMapper.class);

	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "categoryId", source = "category.id")
	JobPostDto jobPostToJobPostDto(JobPost jobPost);

	@Mapping(target = "views", ignore = true)
	//	@Mapping(target = "isActive", defaultValue = "true")
	//	@Mapping(target = "isPublic", defaultValue = "true")
	//	@Mapping(target = "canApply", defaultValue = "true")
	//	@Mapping(target = "isDeleted", defaultValue = "false")
	//	@Mapping(target = "blind", defaultValue = "false")
	//	@Mapping(target = "deaf", defaultValue = "false")
	//	@Mapping(target = "communicationDis", defaultValue = "false")
	//	@Mapping(target = "handDis", defaultValue = "false")
	//	@Mapping(target = "labor", defaultValue = "false")
	@Mapping(target = "isActive", ignore = true)
	@Mapping(target = "isPublic", ignore = true)
	@Mapping(target = "canApply", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	@Mapping(target = "applications", ignore = true)
	@Mapping(target = "viewedUsers", ignore = true)
	@Mapping(target = "createdEmployer", ignore = true)
	@Mapping(target = "savedCandidates", ignore = true)
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "expiryDate", ignore = true)
	@Mapping(target = "id", ignore = true)
	JobPost jobPostUserPostRequestToJobPost(JobPostUserPostRequest jobPostUserPostRequest);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "expiryDate", ignore = true)
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
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "expiryDate", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "applications", ignore = true)
	@Mapping(target = "viewedUsers", ignore = true)
	@Mapping(target = "createdEmployer", ignore = true)
	@Mapping(target = "savedCandidates", ignore = true)
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "category", ignore = true)
	void updateJobPostFromJobPostAdminPostRequest(JobPostAdminPostRequest jobPostAdminPostRequest, @MappingTarget JobPost jobPost);
}
