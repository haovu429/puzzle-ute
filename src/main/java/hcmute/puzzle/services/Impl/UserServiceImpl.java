package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.RoleRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.UserService;
import hcmute.puzzle.utils.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  @Autowired private Converter converter;
  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private RoleRepository roleRepository;

  public boolean checkEmailExists(String email) {
    UserEntity user = userRepository.getUserByEmail(email);
    return user == null;
  }

  @Override
  public ResponseObject save(UserDTO userDTO) {

    // Cast DTO --> Entity
    UserEntity userEntity = converter.toEntity(userDTO);
    // Check Email Exists
    if (!checkEmailExists(userEntity.getEmail())) {
      throw new CustomException("Email already exists");
    }
    //  hash password
    userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));

    UserDTO responseDTO = converter.toDTO(userEntity);
    // don't return password
    responseDTO.setPassword(null);
    // Luu vao dataabase
    userEntity = userRepository.save(userEntity);
    return new ResponseObject(HttpStatus.OK.value(), "Create user success", responseDTO);
  }

  @Override
  public ResponseObject update(long id, UserDTO userDTO) {
    userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

    Optional<UserEntity> oldUser = userRepository.findById(id);

    if (oldUser.isEmpty()) {
      throw new CustomException("This account isn't exists");
    }

    UserEntity userEntity = converter.toEntity(userDTO);

    UserEntity newUser =
        userRepository
            .findById(id)
            .map(
                user -> {
                  user.setUsername(oldUser.get().getUsername());
                  user.setEmail(oldUser.get().getEmail());
                  user.setAvatar(userEntity.getAvatar());
                  user.setPhone(userEntity.getPhone());
                  // user.setOnline(userEntity.isOnline());
                  // user.setLastOnline(userEntity.getLastOnline());
                  user.setActive(oldUser.get().isActive());
                  user.setPassword(oldUser.get().getPassword());
                  user.setRoles(oldUser.get().getRoles());
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
    Set<UserDTO> userDTOS =
        userRepository.findAll().stream()
            .map(
                userEntity -> {
                  UserDTO userDTO = converter.toDTO(userEntity);
                  userDTO.setPassword(null);
                  return userDTO;
                })
            .collect(Collectors.toSet());

    return new ResponseObject(HttpStatus.OK.value(), "Get all user success", userDTOS);
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

  @Override
  public void processOAuthPostLogin(String username) {
    UserEntity existUser = userRepository.getUserByEmail(username);

    if (existUser == null) {
      UserEntity newUser = new UserEntity();
      newUser.setEmail(username);
      newUser.setProvider(Provider.GOOGLE);
      newUser.setActive(true);

      userRepository.save(newUser);
    }

  }

  //  @Override
  //  public ResponseObject getOne() {
  //    // Lay Tat Ca UserEntity
  //    List<UserEntity> userEntitys = userRepository.findAll();
  //    // Tao UserDTO de tra ve Json
  //    List<UserDTO> userDTOs = new ArrayList<>();
  //
  //    // Cast UserEntity --> UserDTO
  //    for (UserEntity i : userEntitys) {
  //      UserDTO user = converter.toDTO(i);
  //      userDTOs.add(user);
  //    }
  //    return new ResponseObject(HttpStatus.OK.value(), "Get all user success", userDTOs);
  //  }

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
