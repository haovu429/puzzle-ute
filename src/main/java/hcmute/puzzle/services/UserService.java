package hcmute.puzzle.services;

import freemarker.template.TemplateException;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.TokenEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.model.enums.FileCategory;
import hcmute.puzzle.model.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface UserService {
  UserEntity save(UserDTO userDTO);

  DataResponse update(long id, UpdateUserPayload userPayload);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getOne(long id);

  ResponseObject getUserByAccount(String email, String password);

  ResponseObject getAccountAmount();

  ResponseObject getListDataUserJoinLastNumWeeks(long numWeek);

  DataResponse updateForAdmin(long id, UserDTO userPayload);

  DataResponse updateAvatarForUser(
      UserEntity userEntity, MultipartFile file, FileCategory fileCategory)
      throws NotFoundException;

  // void processOAuthPostLogin(String username);

  // UserDetails loadUserByUsername(String email);
  void sendMailForgotPwd(String receiveMail, String urlResetPass, TokenEntity token)
      throws InterruptedException, MessagingException, TemplateException, IOException, ExecutionException;
}
