package hcmute.puzzle.services.impl;

import freemarker.template.TemplateException;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.dtos.news.*;
import hcmute.puzzle.infrastructure.dtos.request.CreateJsonDataTypeCvRequest;
import hcmute.puzzle.infrastructure.dtos.request.UpdateJsonDataTypeCvRequest;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.JsonDataMapper;
import hcmute.puzzle.infrastructure.mappers.UserMapper;
import hcmute.puzzle.infrastructure.models.DataStaticJoinAccount;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.FileType;
import hcmute.puzzle.infrastructure.models.enums.JsonDataType;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.repository.JsonDataRepository;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.Provider;
import hcmute.puzzle.utils.RedisUtils;
import hcmute.puzzle.utils.TimeUtil;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	FilesStorageService storageService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JsonDataRepository jsonDataRepository;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private JsonDataMapper jsonDataMapper;

	public boolean checkEmailExists(String email) {
		User user = userRepository.getUserByEmail(email);
		return user == null;
	}

	public boolean checkUsernameExists(String username) {
		User user = userRepository.getUserByUsername(username);
		return user != null;
	}


	public Optional<User> registerUser(RegisterUserDto registerUserDto) {
		return Optional.of(registerUserForAdmin(CreateUserForAdminDto.builder()
																	 .email(registerUserDto.getEmail())
																	 .password(registerUserDto.getPassword())
																	 .isActive(true)
																	 .build(), false));
	}

	@Transactional
	public User registerUserForAdmin(CreateUserForAdminDto userDto, boolean admin) {
		// Check Email Exists
		if (!checkEmailExists(userDto.getEmail())) {
			throw new AlreadyExistsException("Email already exists");
		}

		if (userDto.getUsername() != null && userDto.getUsername().trim().isEmpty()) {
			userDto.setUsername(null);
		}

		// Check username Exists
		if (userDto.getUsername() != null && checkUsernameExists(userDto.getUsername())) {
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

			if (userDto.getFullName() != null && !userDto.getFullName().isEmpty()) {
				user.setFullName(userDto.getFullName());
			} else if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
				String[] split = userDto.getEmail().split("@");
				String tempName = split[0];
				user.setFullName(tempName);
			}

			user.setPhone(userDto.getPhone());
			user.setIsActive(userDto.isActive());
			user.setLocale(user.getLocale());
			user.setProvider(Provider.LOCAL);
			user.setEmailVerified(user.getEmailVerified());
			roleCodes.addAll(userDto.getRoleCodes());
		} else {
			user.setIsActive(false);
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

	private boolean isValidPassword(String password) {

		// Regex to check valid password.
		String regex = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[@#$%^&+=])" + "(?=\\S+$).{6,}$";

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

	@Transactional
	public UserPostDto update(long id, UpdateUserDto user) {

		if (user.getUsername() != null && user.getUsername().trim().isEmpty()) {
			user.setUsername(null);
		}

		UpdateUserForAdminDto updateUserForAdminDto = UpdateUserForAdminDto.builder()
																		   .username(user.getUsername())
																		   .avatar(user.getAvatar())
																		   .fullName(user.getFullName())
																		   .phone(user.getPhone())
																		   .build();
		return updateUserForAdmin(id, updateUserForAdminDto);
	}

	@Transactional
	public UserPostDto updateUserForAdmin(long id, UpdateUserForAdminDto user) {
		User updateUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("NOT_FOUND_USER"));
		// Check username Exists
		if (updateUser.getUsername() != null && !updateUser.getUsername()
														   .equals(user.getUsername()) && checkUsernameExists(
				user.getUsername())) {
			throw new AlreadyExistsException("Username already exists");
		}
		userMapper.updateUserFromDto(user, updateUser);
		if (user.getRoleCodes() != null && !user.getRoleCodes().isEmpty()) {

			if (updateUser.getRoles() != null && !updateUser.getRoles().isEmpty()) {
				// Remove old roles
				updateUser.getRoles().clear();
				userRepository.save(updateUser);
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

		// user must re-login
		//redisUtils.delete(updateUser.getEmail());

		return userPostDTO;
	}

	@Transactional
	public void setRoleWithCreateAccountTypeUser(List<String> roleCodes, User user) {
		if (roleCodes != null && !roleCodes.isEmpty()) {
			Set<Role> roleFromDB = new HashSet<>();
			for (String code : roleCodes) {
				Role role = roleRepository.findByCode(code)
										  .orElseThrow(() -> new NotFoundException("NOT_FOUND_ROLE: + " + code));
				if (role.getCode().equalsIgnoreCase(Roles.CANDIDATE.getValue())) {
					Candidate candidate = Candidate.builder()
												   .emailContact(user.getEmail())
												   .firstName(user.getFullName())
												   .build();
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

	@Transactional
	public UserPostDto updateForAdmin(long id, UserPostDto userPayload) {

		User oldUser = userRepository.findById(id)
									 .orElseThrow(() -> new NotFoundDataException("This account isn't exists"));

		if (userPayload.getUsername() != null) {
			oldUser.setUsername(userPayload.getUsername());
		}
		if (userPayload.getPhone() != null) {
			oldUser.setPhone(userPayload.getPhone());
		}
		if (userPayload.getFullName() != null) {
			//      oldUser.get().setFullName(userPayload.getFullName());
		}

		if (userPayload.getEmailVerified() != oldUser.getEmailVerified()) {
			oldUser.setEmailVerified(userPayload.getEmailVerified());
		}

		if (!oldUser.getIsAdmin()) {
			oldUser.setIsActive(userPayload.getIsActive());
		}

		if (userPayload.getRoleCodes() != null && !userPayload.getRoleCodes().isEmpty()) {
			Set<Role> roleEntities = new HashSet<>();
			for (String code : userPayload.getRoleCodes()) {
				Role role = roleRepository.findByCode(code.toLowerCase())
										  .orElseThrow(() -> new NotFoundDataException("Not found role"));
				if (role.getName().equals(Roles.CANDIDATE.getValue())) {
					Candidate candidate = new Candidate();
					oldUser.setCandidate(candidate);
				}

				if (role.getName().equals(Roles.EMPLOYER.getValue())) {
					Employer employer = new Employer();
					oldUser.setEmployer(employer);
				}
				if (role != null) {
					roleEntities.add(role);
				}
			}
			oldUser.setRoles(roleEntities);
			// xoá token hiện tại --> bắt người dùng dăng nhập lại
			redisUtils.delete(oldUser.getEmail());
		}

		userRepository.save(oldUser);
		UserPostDto userPostDTO = userMapper.userToUserPostDto(oldUser);

		return userPostDTO;
	}

	public UserPostDto updateAvatarForUser(User user, MultipartFile file, FileCategory fileCategory) throws
			NotFoundException {
		String imageUrl = storageService.uploadFileWithFileTypeReturnUrl(user.getEmail(), file, FileType.IMAGE,
																		 fileCategory, true)
										.orElseThrow(() -> new FileStorageException("UPLOAD_FILE_FAILURE"));

		user.setAvatar(imageUrl);
		userRepository.save(user);
		UserPostDto userPostDTO = userMapper.userToUserPostDto(user);
		return userPostDTO;
	}

	@Transactional
	public void delete(long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundDataException("User not found"));

		// delete roles of user
		user.getRoles().clear();
		userRepository.save(user);
		userRepository.delete(user);
	}

	public Page<UserPostDto> getAll(Pageable pageable) {
		// Lay Tat Ca UserEntity
		Page<User> users = userRepository.findAll(pageable);
		Page<UserPostDto> userPostDtos = users.map(userMapper::userToUserPostDto);
		return userPostDtos;
	}


	public UserPostDto getOne(long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Account isn't exists"));
		user.getRoles().add(new Role("user"));
		userRepository.save(user);

		return userMapper.userToUserPostDto(user);
	}

	public UserPostDto getUserByAccount(String email, String password) {
		User user = userRepository.getUserByEmailAndPassword(email, password);
		if (user != null) {
			return userMapper.userToUserPostDto(user);
		} else {
			throw new CustomException("User not found");
		}
	}


	public long getAccountAmount() {
		long count = userRepository.count();
		return count;
	}


	public List<DataStaticJoinAccount> getListDataUserJoinLastNumWeeks(long numWeek) {
		Date timeline = new Date(); // khoi tao tg hien tai
		// Date nextDate = new Date();
		List<DataStaticJoinAccount> data = new ArrayList<>();
		TimeUtil timeUtil = new TimeUtil();
		for (int i = 0; i < numWeek; i++) {
			Date backwardTime = timeUtil.upDownTime_TimeUtil(timeline, 7, 0, 0);
			long count = userRepository.getCountUserJoinInTime(backwardTime, timeline);
			// data.add(count);
			DataStaticJoinAccount dataStaticJoinAccount = new DataStaticJoinAccount("Tuan " + (numWeek - i), count);
			data.add(dataStaticJoinAccount);
			//System.out.println("Tuan " + (numWeek - i) + ": " + count);
			timeline = backwardTime;
		}

		Collections.reverse(data);

		return data;
	}

	public List<JsonDataDto> getMyCvOnlineJsonData() {
		User currentUser = currentUserService.getCurrentUser();
		List<JsonDataDto> jsonDataDtos = jsonDataRepository.findByCreatedByAndType(currentUser.getEmail(),
																				   JsonDataType.CV_ONLINE)
														   .stream()
														   .map(jsonDataMapper::jsonDataToJsonDataDto)
														   .toList();
		return jsonDataDtos;
	}

	public JsonDataDto getJsonDataTypeCvById(long jsonDataId) {
		User currentUser = currentUserService.getCurrentUser();
		JsonData jsonData = jsonDataRepository.findById(jsonDataId).orElseThrow(
				() -> new NotFoundDataException("Not found json data")
		);
		if (!jsonData.getCreatedBy().equals(currentUser.getEmail())) {
			throw new UnauthorizedException("You don't have rights for this data");
		}
		return jsonDataMapper.jsonDataToJsonDataDto(jsonData);
	}

	public JsonDataDto addCvOnlineJsonData(CreateJsonDataTypeCvRequest createJsonDataTypeCvRequest) {
		JsonData jsonData = jsonDataMapper.createJsonDataTypeCvRequestToJsonData(createJsonDataTypeCvRequest);
		jsonData.setType(JsonDataType.CV_ONLINE);
		return jsonDataMapper.jsonDataToJsonDataDto(jsonDataRepository.save(jsonData));
	}

	public JsonDataDto updateCvOnlineJsonData(long jsonDataId, UpdateJsonDataTypeCvRequest updateJsonDataTypeCvRequest) {
		JsonData jsonData = jsonDataRepository.findById(jsonDataId).orElseThrow(
				() -> new NotFoundDataException("Not found json data")
		);
		User currentUser = currentUserService.getCurrentUser();
		if (!jsonData.getCreatedBy().equals(currentUser.getEmail())) {
			throw new UnauthorizedException("You don't have rights for this data");
		}
		jsonDataMapper.updateJsonDataFromJsonDataUpdateRequest(updateJsonDataTypeCvRequest, jsonData);
		return jsonDataMapper.jsonDataToJsonDataDto(jsonDataRepository.save(jsonData));
	}

	public void deleteCvOnlineJsonData(long jsonDataId) {
		JsonData jsonData = jsonDataRepository.findById(jsonDataId).orElseThrow(
				() -> new NotFoundDataException("Not found json data")
		);
		User currentUser = currentUserService.getCurrentUser();
		if (!jsonData.getCreatedBy().equals(currentUser.getEmail())) {
			throw new UnauthorizedException("You don't have rights for this data");
		}
		jsonDataRepository.delete(jsonData);
	}

}
