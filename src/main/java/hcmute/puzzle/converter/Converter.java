package hcmute.puzzle.converter;

import hcmute.puzzle.dto.ApplicationDTO;
import hcmute.puzzle.dto.RoleDTO;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.ApplicationEntity;
import hcmute.puzzle.entities.RoleEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    userDTO.setIsOnline(entity.isOnline());
    userDTO.setJoinDate(entity.getJoinDate());
    userDTO.setLastOnline(entity.getLastOnline());
    userDTO.setActive(entity.isActive());

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
    userEntity.setOnline(dto.getIsOnline());
    userEntity.setJoinDate(dto.getJoinDate());
    userEntity.setLastOnline(dto.getLastOnline());
    userEntity.setActive(dto.isActive());
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

  // ApplicationEntity
  public ApplicationDTO toDTO(ApplicationEntity entity) {
    ApplicationDTO applicationDTO = new ApplicationDTO();
    applicationDTO.setId(entity.getId());
    applicationDTO.setResult(entity.getResult());
    applicationDTO.setNote(entity.getNote());

    if (entity.getCandidateEntity() != null) {
      applicationDTO.setCandidateId(entity.getId());
    }

    if (entity.getJobPostEntity() != null) {
      applicationDTO.setJobPostId(entity.getJobPostEntity().getId());
    }

    return applicationDTO;
  }

  public ApplicationEntity toEntity(ApplicationDTO dto) {
    ApplicationEntity entity = new ApplicationEntity();
    entity.setId(dto.getId());
    entity.setResult(dto.getResult());
    entity.setNote(dto.getNote());
    //entity.setCandidateEntity(dto.getCandidateId());
    return entity;
  }
}
