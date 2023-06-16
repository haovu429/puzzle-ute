package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.entities.Company;
import hcmute.puzzle.infrastructure.dtos.request.CreateCompanyAdminRequest;
import hcmute.puzzle.infrastructure.dtos.request.CreateCompanyRoleUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    CompanyDto CreateCompanyAdminDtoToCompanyDto(CreateCompanyAdminRequest createCompanyAdminRequest);


    //    @Mapping(target = "username", source = "username")
    //    @Mapping(target = "email", source = "email")
    //    @Mapping(target = "phone", source = "phone")
    //    @Mapping(target = "avatar", source = "avatar")
    //    @Mapping(target = "fullName", source = "fullName")
    //    @Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
    //    UserPostDto mapToUserPostDto(Map<String, Object> map);

}
