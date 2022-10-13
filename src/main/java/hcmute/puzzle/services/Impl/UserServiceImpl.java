package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired private Converter converter;
  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  public boolean checkEmailExists(String email) {
    UserEntity user = userRepository.getUserByEmail(email);
    return user == null;
  }

  @Override
  public ResponseObject save(UserDTO userDTO) {
    //  hash password
    userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

    // Cast DTO --> Entity
    UserEntity userEntity = converter.toEntity(userDTO);

    // Check Role Input, Neu K tim thay Role ==> Cancel
    if (userEntity.getRoles().size() == 0) {
      throw new CustomException("Role not found");
    }

    // Check Email Exists
    if (!checkEmailExists(userEntity.getEmail())) {
      throw new CustomException("Email already exists");
    }

    UserDTO responseDTO = converter.toDTO(userEntity);
    // don't return password
    responseDTO.setPassword(null);
    // Luu vao dataabase
    userEntity = userRepository.save(userEntity);
    return new ResponseObject(
        HttpStatus.OK.value(), "Create user success", responseDTO);
  }

  @Override
  public ResponseObject update(long id, UserDTO userDTO) {
    userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

    UserEntity userEntity = converter.toEntity(userDTO);

    if (userEntity.getRoles().size() == 0) {
      throw new CustomException("Role not found");
    } else {
      UserEntity newUser =
          userRepository
              .findById(id)
              .map(
                  user -> {
                    user.setUsername(userEntity.getUsername());
                    user.setEmail(userEntity.getEmail());
                    user.setAvatar(userEntity.getAvatar());
                    user.setPhone(userEntity.getPhone());
                    user.setOnline(userEntity.isOnline());
                    user.setLastOnline(userEntity.getLastOnline());
                    user.setActive(userEntity.isActive());
                    user.setPassword(userEntity.getPassword());
                    user.setRoles(userEntity.getRoles());
                    return userRepository.save(user);
                  })
              .orElse(null);

      if (newUser == null) {
        throw new CustomException("User not found");
      } else {
        return new ResponseObject(
            HttpStatus.OK.value(), "Update user success", converter.toDTO(newUser));
      }
    }
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = userRepository.existsById(id);
    if (exists) {
      userRepository.deleteById(id);
      return new ResponseObject(HttpStatus.OK.value(), "Delete user success", "");
    } else {
      throw new CustomException("User not found");
    }
  }

  @Override
  public ResponseObject getAll() {
    // Lay Tat Ca UserEntity
    List<UserEntity> userEntitys = userRepository.findAll();
    // Tao UserDTO de tra ve Json
    List<UserDTO> userDTOs = new ArrayList<>();

    // Cast UserEntity --> UserDTO
    for (UserEntity i : userEntitys) {
      UserDTO user = converter.toDTO(i);
      userDTOs.add(user);
    }
    return new ResponseObject(HttpStatus.OK.value(), "Get all user success", userDTOs);
  }

  @Override
  public ResponseObject getUserByAccount(String email, String password) {
    // TODO Auto-generated method stub
    UserEntity userEntity = userRepository.getUserByAccount(email, password);
    if (userEntity != null) {
      return new ResponseObject(
          HttpStatus.OK.value(), "Get user success", converter.toDTO(userEntity));
    } else {
      throw new CustomException("User not found");
    }
  }

  //  @Override
  //  public UserDetails loadUserByUsername(String email) {
  //    // Kiểm tra xem user có tồn tại trong database không?
  //    UserEntity user = userRepository.findByEmail(email);
  //    if (user == null) {
  //      throw new UsernameNotFoundException(email);
  //    }
  //    return new CustomUserDetails(user);
  //  }
}
