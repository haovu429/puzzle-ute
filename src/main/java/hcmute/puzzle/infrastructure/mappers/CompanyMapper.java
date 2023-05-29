package hcmute.puzzle.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring"
)
public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

//    @Mapping(target = "username", source = "username")
//    @Mapping(target = "email", source = "email")
//    @Mapping(target = "phone", source = "phone")
//    @Mapping(target = "avatar", source = "avatar")
//    @Mapping(target = "fullName", source = "fullName")
//    @Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
//    CompanyDto userToUserPostDto(CompanyEntity entity);


//    @Mapping(target = "username", source = "username")
//    @Mapping(target = "email", source = "email")
//    @Mapping(target = "phone", source = "phone")
//    @Mapping(target = "avatar", source = "avatar")
//    @Mapping(target = "fullName", source = "fullName")
//    @Mapping(target = "roleCodes", qualifiedByName = "RolesToStrings")
//    UserPostDto mapToUserPostDto(Map<String, Object> map);

}
