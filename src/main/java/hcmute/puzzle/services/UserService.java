package hcmute.puzzle.services;

import freemarker.template.TemplateException;
import hcmute.puzzle.infrastructure.dtos.news.*;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.TokenEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface UserService {
  Optional<UserEntity> registerUser(RegisterUserDto registerUserDto);

  UserEntity registerUserForAdmin(CreateUserForAdminDto userForAdminDto, boolean admin);

  DataResponse update(long id, UpdateUserDto user);

  DataResponse updateUserForAdmin(long id, UpdateUserForAdminDto user);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getOne(long id);

  ResponseObject getUserByAccount(String email, String password);

  ResponseObject getAccountAmount();

  ResponseObject getListDataUserJoinLastNumWeeks(long numWeek);

  DataResponse updateForAdmin(long id, UserPostDto userPayload);

  DataResponse updateAvatarForUser(
      UserEntity userEntity, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException;

  // void processOAuthPostLogin(String username);

  // UserDetails loadUserByUsername(String email);
  void sendMailForgotPwd(String receiveMail, String urlResetPass, TokenEntity token)
      throws InterruptedException, MessagingException, TemplateException, IOException, ExecutionException;

  void sendMailVerifyAccount(String receiveMail, String verifyAccountUrl, TokenEntity token)
          throws InterruptedException, MessagingException, TemplateException, IOException, ExecutionException;
}
