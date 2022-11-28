package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;

public interface UserService {
  ResponseObject save(UserDTO userDTO);

  ResponseObject update(long id, UserDTO userDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getOne(long id);

  ResponseObject getUserByAccount(String email, String password);

  void processOAuthPostLogin(String username);

  // UserDetails loadUserByUsername(String email);
}
