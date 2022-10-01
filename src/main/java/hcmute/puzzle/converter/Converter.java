package hcmute.puzzle.converter;

import hcmute.puzzle.dto.RoleDTO;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.RoleEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Converter {

  @Autowired private RoleRepository roleRepository;
  // User
  public UserDTO toDTO(UserEntity entity) {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(entity.getId());
    userDTO.setUsername(entity.getUsername());
    userDTO.setEmail(entity.getEmail());
    userDTO.setPassword(entity.getPassword());
    userDTO.setPhone(entity.getPhone());
    userDTO.setAvatar(entity.getAvatar());
    userDTO.setIsOnline(entity.getIsOnline());
    userDTO.setJoinDate(entity.getJoinDate());
    userDTO.setLastOnline(entity.getLastOnline());
    userDTO.setIsActive(entity.getIsActive());

    return userDTO;
  }

  public UserEntity toEntity(UserDTO dto) {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(dto.getId());
    userEntity.setUsername(dto.getUsername());
    userEntity.setEmail(dto.getEmail());
    userEntity.setPassword(dto.getPassword());
    userEntity.setPhone(dto.getPhone());
    userEntity.setAvatar(dto.getAvatar());
    userEntity.setIsOnline(dto.getIsOnline());
    userEntity.setJoinDate(dto.getJoinDate());
    userEntity.setLastOnline(dto.getLastOnline());
    userEntity.setIsActive(dto.getIsActive());
//        List<RoleEntity> roleEntities = dto.getRoles().stream().map(role -> toEntity(role))
//                .collect(Collectors.toList());

    List<RoleEntity> roleEntities = new ArrayList<>();
    for (String code : dto.getRoleCodes()) {
      if (roleRepository.existsById(code)) {
        roleEntities.add(roleRepository.findOneByCode(code));
      } else {
        throw new CustomException("Can't convert! Not found role has role code = " + code);
      }
    }
    userEntity.setRoles(roleEntities);

    return userEntity;
  }

  //  Role
  public RoleDTO toDTO(RoleEntity entity) {
    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setCode(entity.getCode());
    roleDTO.setName(entity.getName());
    return roleDTO;
  }

  public RoleEntity toEntity(RoleDTO dto) {
    RoleEntity roleEntity = new RoleEntity();
    roleEntity.setCode(dto.getCode());
    roleEntity.setName(dto.getName());
    return roleEntity;
  }
}
