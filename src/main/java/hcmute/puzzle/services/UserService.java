package hcmute.puzzle.services;

import freemarker.template.TemplateException;
import hcmute.puzzle.infrastructure.dtos.news.*;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Token;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.models.DataStaticJoinAccount;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

//import javax.mail.MessagingException;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface UserService {
  Optional<User> registerUser(RegisterUserDto registerUserDto);

  User registerUserForAdmin(CreateUserForAdminDto userForAdminDto, boolean admin);

  UserPostDto update(long id, UpdateUserDto user);

  UserPostDto updateUserForAdmin(long id, UpdateUserForAdminDto user);

  void delete(long id);

  Page<UserPostDto> getAll(Pageable pageable);

  UserPostDto getOne(long id);

  UserPostDto getUserByAccount(String email, String password);

  long getAccountAmount();

  List<DataStaticJoinAccount> getListDataUserJoinLastNumWeeks(long numWeek);

  UserPostDto updateForAdmin(long id, UserPostDto userPayload);

  public void prepareForRole(User user);

  UserPostDto updateAvatarForUser(
      User user, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException;
}
