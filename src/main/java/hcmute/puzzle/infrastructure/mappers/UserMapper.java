package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.news.UpdateUserForAdminDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.entities.RoleEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;


@Mapper(
        componentModel =  MappingConstants.ComponentModel.CDI
)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    //@Mapping(target = "roleCodes", ignore = true)
    UserPostDto userToUserPostDto(UserEntity entity);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "fullName", source = "fullName")
    UserPostDto mapToUserPostDto(Map<String, String> map);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true)
    void updateUserFromDto(UpdateUserForAdminDto dto, @MappingTarget UserEntity entity);

    UserEntity dtoToUser(UserPostDto userPostDTO);

//    @Named("RolesToStrings")
//    default List<String> defaultRoleValueForQualifier(List<RoleEntity> roles) {
//        List<String> roleCodes = new ArrayList<>();
//        roleCodes = roles.stream().map(role -> role.getCode().
//                toUpperCase()).collect(Collectors.toList());
//        return  roleCodes;
//    }

    default String roleToString(RoleEntity role) {
        return role.getCode().toUpperCase();
    }

//    @IterableMapping()
//    List<String> roles(List<RoleEntity> roles);

}
