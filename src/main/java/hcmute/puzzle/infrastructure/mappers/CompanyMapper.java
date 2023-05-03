package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.entities.CompanyEntity;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
    CompanyDto userToUserPostDto(CompanyEntity entity);



    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
    UserPostDto mapToUserPostDto(Map<String, Object> map);

}
