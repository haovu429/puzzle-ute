package hcmute.puzzle.services.impl;

import freemarker.template.TemplateException;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.news.*;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.UserMapper;
import hcmute.puzzle.infrastructure.models.DataStaticJoinAccount;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.services.SecurityService;
import hcmute.puzzle.services.UserService;
import hcmute.puzzle.utils.Provider;
import hcmute.puzzle.utils.RedisUtils;
import hcmute.puzzle.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired private Converter converter;
  @Autowired private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  FilesStorageService storageService;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private RedisUtils redisUtils;

  @Autowired
  private MailService mailService;

  @Autowired
  private SecurityService securityService;

  public boolean checkEmailExists(String email) {
    User user = userRepository.getUserByEmail(email);
    return user == null;
  }

  public boolean checkUsernameExists(String username) {
    User user = userRepository.getUserByUsername(username);
    return user != null;
  }

  @Override
  public Optional<User> registerUser(RegisterUserDto registerUserDto) {
    return Optional.of(registerUserForAdmin(CreateUserForAdminDto.builder()
            .email(registerUserDto.getEmail())
            .password(registerUserDto.getPassword())
            .build(), false));
  }

  @Transactional
  @Override
  public User registerUserForAdmin(CreateUserForAdminDto userDto, boolean admin) {
    // Check Email Exists
    if (!checkEmailExists(userDto.getEmail())) {
      throw new AlreadyExistsException("Email already exists");
    }

    // Check username Exists
    if (checkUsernameExists(userDto.getUsername())) {
      throw new AlreadyExistsException("Username already exists");
    }

    //    if (!isValidPassword(userDto.getPassword())) {
    //      throw new InvalidException("Constraint passes length more than six and contains at least one special character," +
    //              " number, uppercase, lowercase");
    //    }
    //  hash password
    User user = User.builder()
                    .email(userDto.getEmail())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .build();

    List<String> roleCodes = new ArrayList<>();
    if (admin) {
      user.setUsername(userDto.getUsername());
      user.setAvatar(userDto.getAvatar());
      //      user.setFullName(userDto.getFullName());
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
    user = userRepository.save(user);
    List<Role> rolesFromDb = roleRepository.findAllByCodeIn(roleCodes);
    if (rolesFromDb == null || rolesFromDb.isEmpty()) {
      throw new NotFoundException("NOT_FOUND_ROLE");
    }
    setRoleWithCreateAccountTypeUser(rolesFromDb.stream().map(Role::getCode).collect(Collectors.toList()), user);
    // Save to DB
    user = userRepository.save(user);
    try {
      securityService.sendTokenVerifyAccount(user.getEmail());
    } catch (MessagingException | ExecutionException | IOException | TemplateException | InterruptedException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
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

  @Override
  public DataResponse update(long id, UpdateUserDto user) {
    // Check username Exists
    if (checkUsernameExists(user.getUsername())) {
      throw new AlreadyExistsException("Username already exists");
    }
    UpdateUserForAdminDto updateUserForAdminDto = UpdateUserForAdminDto.builder()
            .username(user.getUsername())
            .avatar(user.getAvatar())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .build();

    return updateUserForAdmin(id, updateUserForAdminDto);
  }

  public DataResponse updateUserForAdmin(long id, UpdateUserForAdminDto user) {

    User updateUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("NOT_FOUND_USER"));
    UserMapper.INSTANCE.updateUserFromDto(user, updateUser);
    if (user.getRoleCodes() != null && !user.getRoleCodes().isEmpty()){

      if (updateUser.getRoles() != null && !updateUser.getRoles().isEmpty()) {
        // Remove old roles
        roleRepository.deleteAll(updateUser.getRoles());
      }

      List<Role> rolesFromDb = roleRepository.findAllByCodeIn(user.getRoleCodes());
      if (rolesFromDb == null || rolesFromDb.isEmpty()) {
        throw new NotFoundException("NOT_FOUND_ROLE");
      }
      setRoleWithCreateAccountTypeUser(rolesFromDb.stream().map(Role::getCode).collect(Collectors.toList()),
                                       updateUser);
    }
    UserMapper mapper = UserMapper.INSTANCE;

    UserPostDto userPostDTO = mapper.userToUserPostDto(
            userRepository.save(updateUser));// converter.toDTO(userRepository.save(updateUser));
    redisUtils.delete(updateUser.getEmail());

    return new DataResponse(userPostDTO);
  }

  @Transactional
  public void setRoleWithCreateAccountTypeUser(List<String> roleCodes, User user) {
    if (roleCodes != null && !roleCodes.isEmpty()) {
      Set<Role> roleFromDB = new HashSet<>();
      for (String code : roleCodes) {
        Role role = roleRepository.findByCode(code)
                                  .orElseThrow(() -> new NotFoundException("NOT_FOUND_ROLE: + " + code));
        if (role.getCode().equalsIgnoreCase(Roles.CANDIDATE.getValue())) {
          Candidate candidate = Candidate.builder().emailContact(user.getEmail()).firstName(user.getFullName()).build();
          candidate.setUser(user);
          user.setCandidate(candidate);
        } else if (role.getName().equalsIgnoreCase(Roles.EMPLOYER.getValue())) {
          Employer employer = Employer.builder().firstName(user.getFullName()).build();
          employer.setUser(user);
          user.setEmployer(employer);
        }
        roleFromDB.add(role);
      }
      user.setRoles(roleFromDB);
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void prepareForRole(User user) {
    if (Objects.isNull(user.getRoles())) {
      return;
    }
    user.getRoles().forEach(role -> {
      if (role.getCode().equalsIgnoreCase(Roles.CANDIDATE.getValue())) {
        Candidate candidate = Candidate.builder().emailContact(user.getEmail()).build();
        candidate.setUser(user);
        user.setCandidate(candidate);
      } else if (role.getName().equalsIgnoreCase(Roles.EMPLOYER.getValue())) {
        Employer employer = Employer.builder().build();
        employer.setUser(user);
        user.setEmployer(employer);
      }
    });
    userRepository.save(user);
  }

  @Override
  public DataResponse updateForAdmin(long id, UserPostDto userPayload) {

    Optional<User> oldUser = userRepository.findById(id);

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
      //      oldUser.get().setFullName(userPayload.getFullName());
    }

    if (userPayload.isEmailVerified() != oldUser.get().isEmailVerified()) {
      oldUser.get().setEmailVerified(userPayload.isEmailVerified());
    }

    // oldUser.get().setAvatar(userPayload.getAvatar());
    // oldUser.get().setAvatar(updateAvatarReturnUrl(oldUser.get().getEmail(), file));
    oldUser.get().setActive(userPayload.isActive());

    if (userPayload.getRoleCodes() != null && !userPayload.getRoleCodes().isEmpty()) {
      Set<Role> roleEntities = new HashSet<>();
      for (String code : userPayload.getRoleCodes()) {
        Role role = roleRepository.findOneByCode(code);
        if (role.getName().equals(Roles.CANDIDATE.getValue())) {
          Candidate candidate = new Candidate();
          oldUser.get().setCandidate(candidate);
        }

        if (role.getName().equals(Roles.EMPLOYER.getValue())) {
          Employer employer = new Employer();
          oldUser.get().setEmployer(employer);
        }
        if (role != null) {
          roleEntities.add(role);
        }
      }
      oldUser.get().setRoles(roleEntities);
      // xoá token hiện tại --> bắt người dùng dăng nhập lại
      redisUtils.delete(oldUser.get().getEmail());
    }


    UserPostDto userPostDTO = converter.toDTO(userRepository.save(oldUser.get()));

    return new DataResponse(userPostDTO);
  }

  public DataResponse updateAvatarForUser(
      User user, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException {
    String imageUrl =
            storageService
                    .uploadFileWithFileTypeReturnUrl(user.getEmail(), file, fileCategory, true)
            .orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));

    user.setAvatar(imageUrl);
    userRepository.save(user);
    UserPostDto userPostDTO = converter.toDTO(user);
    return new DataResponse(userPostDTO);
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = userRepository.existsById(id);
    if (exists) {
      User user = userRepository.findById(id).orElseThrow(() -> new NotFoundDataException("NOT FOUND USER"));
      // delete roles of user
      user.getRoles().clear();
      userRepository.delete(user);
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
    Optional<User> userEntity = userRepository.findById(id);
    userEntity.get().getRoles().add(new Role("user"));
    userRepository.save(userEntity.get());

    return new ResponseObject(
        HttpStatus.OK.value(), "User info", converter.toDTO(userEntity.get()));
  }

  @Override
  public ResponseObject getUserByAccount(String email, String password) {
    User user = userRepository.getUserByEmailAndPassword(email, password);
    if (user != null) {
      return new ResponseObject(
          HttpStatus.OK.value(), "Get user success", converter.toDTO(user));
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

}
