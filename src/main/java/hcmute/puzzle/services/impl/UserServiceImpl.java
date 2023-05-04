package hcmute.puzzle.services.impl;

import freemarker.template.TemplateException;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.news.*;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;

import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.UserMapper;
import hcmute.puzzle.infrastructure.models.DataStaticJoinAccount;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.models.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.services.UserService;
import hcmute.puzzle.utils.Provider;
import hcmute.puzzle.utils.RedisUtils;
import hcmute.puzzle.utils.TimeUtil;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired private Converter converter;
  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired FilesStorageService storageService;

  @Autowired private RoleRepository roleRepository;

  @Autowired private RedisUtils redisUtils;

  public boolean checkEmailExists(String email) {
    UserEntity user = userRepository.getUserByEmail(email);
    return user == null;
  }

  public boolean checkUsernameExists(String username) {
    UserEntity user = userRepository.getUserByUsername(username);
    return user == null;
  }

  @Override
  public UserEntity registerUser(RegisterUserDto registerUserDto) {
    return registerUserForAdmin(CreateUserForAdminDto.builder()
            .email(registerUserDto.getEmail())
            .password(registerUserDto.getPassword())
            .build(), false);
  }

  @Override
  public UserEntity registerUserForAdmin(CreateUserForAdminDto userDto, boolean admin) {
    // Check Email Exists
    if (!checkEmailExists(userDto.getEmail())) {
      throw new AlreadyExistsException("Email already exists");
    }

    // Check username Exists
    if (!checkUsernameExists(userDto.getUsername())) {
      throw new AlreadyExistsException("Username already exists");
    }

    if (!isValidPassword(userDto.getPassword())) {
      throw new InvalidException("Constraint passes length more than six and contains at least one special character," +
              " number, uppercase, lowercase");
    }
    //  hash password
    UserEntity user = new UserEntity();
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));

    List<String> roleCodes = new ArrayList<>();
    if (admin) {
      user.setUsername(userDto.getUsername());
      user.setAvatar(userDto.getAvatar());
      user.setFullName(userDto.getFullName());
      user.setPhone(userDto.getPhone());
      user.setActive(userDto.isActive());
      user.setLocale(user.getLocale());
      user.setProvider(Provider.LOCAL);
      user.setEmailVerified(user.isEmailVerified());
      roleCodes.addAll(userDto.getRoleCodes());
    } else {
      user.setActive(false);
      roleCodes.add("user");
    }
    List<RoleEntity> rolesFromDb = roleRepository.findAllByCodeIn(roleCodes);
    if (rolesFromDb == null || rolesFromDb.isEmpty()) {
      throw new NotFoundException("NOT_FOUND_ROLE");
    }
    setRoleWithCreateAccountTypeUser(
            rolesFromDb.stream().map(
                    RoleEntity::getCode).collect(Collectors.toList()),
            user);

    // Save to DB
    user = userRepository.save(user);
    return user;
  }

  private boolean isValidPassword(String password)
  {

    // Regex to check valid password.
    String regex = "^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{6,}$";

    // Compile the ReGex
    Pattern p = Pattern.compile(regex);

    // If the password is empty
    // return false
    if (password == null) {
      return false;
    }

    // Pattern class contains matcher() method
    // to find matching between given password
    // and regular expression.
    Matcher m = p.matcher(password);

    // Return if the password
    // matched the ReGex
    return m.matches();
  }

  public void sendMailVerifyAccount(String receiveMail, String urlVerify) {}

  @Override
  public DataResponse update(long id, UpdateUserDto user) {
    // Check username Exists
    if (!checkUsernameExists(user.getUsername())) {
      throw new AlreadyExistsException("Username already exists");
    }
    UpdateUserForAdminDto updateUserForAdminDto = UpdateUserForAdminDto.builder()
            .username(user.getUsername())
            .avatar(user.getAvatar())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .build();

    return new DataResponse(updateUserForAdmin(id, updateUserForAdminDto));
  }

  public DataResponse updateUserForAdmin(long id, UpdateUserForAdminDto user) {

    UserEntity updateUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("NOT_FOUND_USER"));
    UserMapper.INSTANCE.updateUserFromDto(user, updateUser);
    if (user.getRoleCodes() != null && !user.getRoleCodes().isEmpty()){

      if (updateUser.getRoles() != null && !updateUser.getRoles().isEmpty()) {
        // Remove old roles
        roleRepository.deleteAll(updateUser.getRoles());
      }

      List<RoleEntity> rolesFromDb = roleRepository.findAllByCodeIn(user.getRoleCodes());
      if (rolesFromDb == null || rolesFromDb.isEmpty()) {
        throw new NotFoundException("NOT_FOUND_ROLE");
      }
      setRoleWithCreateAccountTypeUser(
              rolesFromDb.stream().map(
                      RoleEntity::getCode).collect(Collectors.toList()),
              updateUser);
    }
    UserPostDto userPostDTO = converter.toDTO(userRepository.save(updateUser));
    redisUtils.delete(updateUser.getEmail());

    return new DataResponse(userPostDTO);
  }

  private void setRoleWithCreateAccountTypeUser(List<String> roleCodes, UserEntity user) {
    if (roleCodes!= null && !roleCodes.isEmpty()) {
      for (String code : roleCodes) {
        RoleEntity role = roleRepository.findByCode(code).orElseThrow(
                () -> new NotFoundException("NOT_FOUND_ROLE: + " + code));
        if (role.getName().equals(Roles.CANDIDATE.value)) {
          CandidateEntity candidate = new CandidateEntity();
          user.setCandidateEntity(candidate);
        }

        if (role.getName().equals(Roles.EMPLOYER.value)) {
          EmployerEntity employer = new EmployerEntity();
          user.setEmployerEntity(employer);
        }
        user.getRoles().add(role);
      }
    }
  }

  @Override
  public DataResponse updateForAdmin(long id, UserPostDto userPayload) {

    Optional<UserEntity> oldUser = userRepository.findById(id);

    if (oldUser.isEmpty()) {
      throw new CustomException("This account isn't exists");
    }

    if (userPayload.getUsername() != null) {
      oldUser.get().setUsername(userPayload.getUsername());
    }
    if (userPayload.getPhone() != null) {
      oldUser.get().setPhone(userPayload.getPhone());
    }
    if (userPayload.getFullName() != null) {
      oldUser.get().setFullName(userPayload.getFullName());
    }

    if (userPayload.isEmailVerified() != oldUser.get().isEmailVerified()) {
      oldUser.get().setEmailVerified(userPayload.isEmailVerified());
    }

    // oldUser.get().setAvatar(userPayload.getAvatar());
    // oldUser.get().setAvatar(updateAvatarReturnUrl(oldUser.get().getEmail(), file));
    oldUser.get().setActive(userPayload.isActive());

    if (userPayload.getRoleCodes() != null && !userPayload.getRoleCodes().isEmpty()) {
      Set<RoleEntity> roleEntities = new HashSet<>();
      for (String code : userPayload.getRoleCodes()) {
        RoleEntity role = roleRepository.findOneByCode(code);
        if (role.getName().equals(Roles.CANDIDATE.value)) {
          CandidateEntity candidate = new CandidateEntity();
          oldUser.get().setCandidateEntity(candidate);
        }

        if (role.getName().equals(Roles.EMPLOYER.value)) {
          EmployerEntity employer = new EmployerEntity();
          oldUser.get().setEmployerEntity(employer);
        }
        if (role != null) {
          roleEntities.add(role);
        }
      }
      oldUser.get().setRoles(roleEntities.stream().collect(Collectors.toList()));
      // xoá token hiện tại --> bắt người dùng dăng nhập lại
      redisUtils.delete(oldUser.get().getEmail());
    }


    UserPostDto userPostDTO = converter.toDTO(userRepository.save(oldUser.get()));

    return new DataResponse(userPostDTO);
  }

  public DataResponse updateAvatarForUser(
      UserEntity userEntity, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException {
    FileEntity savedFile =
        storageService
            .uploadFileWithFileTypeReturnUrl(userEntity.getEmail(), file, fileCategory)
            .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));

    userEntity.setAvatar(savedFile.getUrl());
    userRepository.save(userEntity);
    UserPostDto userPostDTO = converter.toDTO(userEntity);
    return new DataResponse(userPostDTO);
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
    Set<UserPostDto> userPostDtos =
        userRepository.findAll().stream()
            .map(
                userEntity -> {
                  UserPostDto userPostDTO = converter.toDTO(userEntity);
                  return userPostDTO;
                })
            .collect(Collectors.toSet());

    return new ResponseObject(HttpStatus.OK.value(), "Get all user success", userPostDtos);
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
      // data.add(count);
      DataStaticJoinAccount dataStaticJoinAccount =
          new DataStaticJoinAccount("Tuan " + (numWeek - i), count);
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

  @Async
  public void sendMailForgotPwd(String receiveMail, String urlResetPass, TokenEntity token)
      throws InterruptedException,
          MessagingException,
          TemplateException,
          IOException,
          ExecutionException {
    MailService mailService = new MailService();
    mailService.executeSendMailWithThread(receiveMail, urlResetPass, token);
    // Thread.sleep(10000);
    log.info("asdasf");
  }
}
