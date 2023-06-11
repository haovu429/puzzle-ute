package hcmute.puzzle.services;

import freemarker.template.TemplateException;
import hcmute.puzzle.infrastructure.dtos.news.*;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Token;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

//import javax.mail.MessagingException;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface UserService {
  Optional<User> registerUser(RegisterUserDto registerUserDto);

  User registerUserForAdmin(CreateUserForAdminDto userForAdminDto, boolean admin);

  DataResponse update(long id, UpdateUserDto user);

  DataResponse updateUserForAdmin(long id, UpdateUserForAdminDto user);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getOne(long id);

  ResponseObject getUserByAccount(String email, String password);

  ResponseObject getAccountAmount();

  ResponseObject getListDataUserJoinLastNumWeeks(long numWeek);

  DataResponse updateForAdmin(long id, UserPostDto userPayload);

  public void prepareForRole(User user);

  DataResponse updateAvatarForUser(
      User user, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException;
}
