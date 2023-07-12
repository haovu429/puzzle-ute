package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.entities.Company;
import hcmute.puzzle.infrastructure.dtos.request.CreateCompanyAdminRequest;
import hcmute.puzzle.infrastructure.dtos.request.CreateCompanyRoleUserRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    @Mapping(target = "createdEmployerId", source = "createdEmployer.id")
    @Mapping(target = "imageFile", ignore = true)
    CompanyDto companyToCompanyDto(Company entity);

    @Mapping(target = "createdEmployerId", source = "createdEmployer.id")
    CompanyResponse companyToCompanyResponse(Company entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdEmployerId", ignore = true)
    CompanyDto createCompanyRoleUserRequestToCompanyDto(CreateCompanyRoleUserRequest createCompanyRoleUserRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "imageFile", source = "image")
    CompanyDto createCompanyAdminDtoToCompanyDto(CreateCompanyAdminRequest createCompanyAdminRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdEmployer", ignore = true)
    @Mapping(target = "followingCandidate", ignore = true)
    @Mapping(target = "jobPostEntities", ignore = true)
    void updateCompanyRoleAdminFromCompanyDto(CreateCompanyAdminRequest createCompanyAdminRequest, @MappingTarget Company company);


    //    @Mapping(target = "username", source = "username")
    //    @Mapping(target = "email", source = "email")
    //    @Mapping(target = "phone", source = "phone")
    //    @Mapping(target = "avatar", source = "avatar")
    //    @Mapping(target = "fullName", source = "fullName")
    //    @Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
    //    UserPostDto mapToUserPostDto(Map<String, Object> map);

}
