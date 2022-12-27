package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.RoleEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.model.DataStaticJoinAccount;
import hcmute.puzzle.model.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.repository.RoleRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.services.UserService;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  @Autowired private Converter converter;
  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired FilesStorageService storageService;

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
  public DataResponse update(long id, UpdateUserPayload userPayload) {

    Optional<UserEntity> oldUser = userRepository.findById(id);

    if (oldUser.isEmpty()) {
      throw new CustomException("This account isn't exists");
    }

    oldUser.get().setUsername(userPayload.getUsername());
    oldUser.get().setPhone(userPayload.getPhone());
    oldUser.get().setFullName(userPayload.getFullName());

    UserDTO userDTO = converter.toDTO(userRepository.save(oldUser.get()));
    userDTO.setPassword(null);

    return new DataResponse(userDTO);
  }

  @Override
  public DataResponse updateForAdmin(long id, UserDTO userPayload) {

    Optional<UserEntity> oldUser = userRepository.findById(id);

    if (oldUser.isEmpty()) {
      throw new CustomException("This account isn't exists");
    }

    oldUser.get().setUsername(userPayload.getUsername());
    oldUser.get().setPhone(userPayload.getPhone());
    oldUser.get().setFullName(userPayload.getFullName());
    //oldUser.get().setAvatar(userPayload.getAvatar());
    //oldUser.get().setAvatar(updateAvatarReturnUrl(oldUser.get().getEmail(), file));
    oldUser.get().setActive(userPayload.isActive());

    Set<RoleEntity> roleEntities = new HashSet<>();
    for (String code : userPayload.getRoleCodes()) {
       RoleEntity role = roleRepository.findOneByCode(code);
       if (role != null) {
         roleEntities.add(role);
       }
    }
    oldUser.get().setRoles(roleEntities);

    UserDTO userDTO = converter.toDTO(userRepository.save(oldUser.get()));
    userDTO.setPassword(null);

    return new DataResponse(userDTO);
  }

  public String updateAvatarReturnUrl(String email, MultipartFile file) {
    String fileName = email + "_avatar";

    Map response = null;

    try {
      // push to storage cloud
      response = storageService.uploadAvatarImage(fileName, file, Constant.STORAGE_IMAGE_LOCATION);

    } catch (Exception e) {
      e.printStackTrace();
    }

    String url = response.get("secure_url").toString();

    return url;
  }

  public DataResponse updateAvatarForUser(UserEntity userEntity, MultipartFile file) {
    String urlAvatar = updateAvatarReturnUrl(userEntity.getEmail(), file);
    userEntity.setAvatar(urlAvatar);
    UserDTO userDTO = converter.toDTO(userEntity);
    userDTO.setPassword(null);
    return new DataResponse(userDTO);

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
  public ResponseObject getOne(long id) {
    boolean exists = userRepository.existsById(id);
    if (!exists) {
      throw new CustomException("Account isn't exists");
    }
    Optional<UserEntity> userEntity = userRepository.findById(id);
    userEntity.get().getRoles().add(new RoleEntity("user"));
    userRepository.save(userEntity.get());

    return new ResponseObject(
        HttpStatus.OK.value(), "User info", converter.toDTO(userEntity.get()));
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
  public ResponseObject getAccountAmount() {
    long count = userRepository.count();

    return new ResponseObject(HttpStatus.OK.value(), "Account amount", count);
  }

  @Override
  public ResponseObject getListDataUserJoinLastNumWeeks(long numWeek) {
    Date timeline = new Date(); // khoi tao tg hien tai
    // Date nextDate = new Date();
    List<DataStaticJoinAccount> data = new ArrayList<>();
    TimeUtil timeUtil = new TimeUtil();
    for (int i = 0; i < numWeek; i++) {
      Date backwardTime = timeUtil.upDownTime_TimeUtil(timeline, 7, 0, 0);
      long count = userRepository.getCountUserJoinInTime(backwardTime, timeline);
      //data.add(count);
      DataStaticJoinAccount dataStaticJoinAccount = new DataStaticJoinAccount("Tuan " + (numWeek-i), count);
      data.add(dataStaticJoinAccount);
      System.out.println("Tuan " + (numWeek - i) + ": " + count);
      timeline = backwardTime;
    }

    Collections.reverse(data);

    return new ResponseObject(
        HttpStatus.OK.value(),
        "Data static for amount of join account in " + numWeek + " weeks",
        data);
  }


}
