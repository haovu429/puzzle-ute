package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.news.UpdateUserForAdminDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.dtos.request.PostCandidateRequest;
import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(
        componentModel =  "spring"
)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roleCodes", source = "roles")
    UserPostDto userToUserPostDto(User entity);

//    @Mapping(target = "username", source = "userName")
//    @Mapping(target = "email", source = "email")
//    @Mapping(target = "phone", source = "phone")
//    @Mapping(target = "avatar", source = "avatar")
//    @Mapping(target = "fullName", source = "fullName")
//    @Mapping(target = "roleCodes", ignore = true)
//    UserPostDto mapToUserPostDto(Map<String, String> map);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "locale", ignore = true)
//    @Mapping(target = "isActive", ignore = true)
//    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
//    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "documentEntities", ignore = true)
    @Mapping(target = "notificationEntities", ignore = true)
    @Mapping(target = "viewJobPosts", ignore = true)
    @Mapping(target = "subscribeEntities", ignore = true)
//    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "isAdmin", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "balance", ignore = true)
    void updateUserFromDto(UpdateUserForAdminDto dto, @MappingTarget User entity);


//    UserEntity dtoToUser(UserPostDto userPostDTO);

//    @Named("RolesToStrings")
//    default List<String> defaultRoleValueForQualifier(List<RoleEntity> roles) {
//        List<String> roleCodes = new ArrayList<>();
//        roleCodes = roles.stream().map(role -> role.getCode().
//                toUpperCase()).collect(Collectors.toList());
//        return  roleCodes;
//    }

    default String roleToString(Role role) {
        return role.getCode().toUpperCase();
    }


    @IterableMapping(elementTargetType = String.class)
    List<String> roles(List<Role> roles);

}
