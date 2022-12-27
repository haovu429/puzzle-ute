package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.model.payload.request.user.UpdateUserPayload;
import hcmute.puzzle.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
  ResponseObject save(UserDTO userDTO);

  DataResponse update(long id, UpdateUserPayload userPayload);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getOne(long id);

  ResponseObject getUserByAccount(String email, String password);

  ResponseObject getAccountAmount();

  ResponseObject getListDataUserJoinLastNumWeeks(long numWeek);

  DataResponse updateForAdmin(long id, UserDTO userPayload);

  DataResponse updateAvatarForUser(UserEntity userEntity, MultipartFile file);

  // void processOAuthPostLogin(String username);

  // UserDetails loadUserByUsername(String email);
}
