package hcmute.puzzle.services;

import hcmute.puzzle.entities.UserEntity;

public interface TokenService {
  UserEntity findUSerByToken(String token);

  void saveToken(String tokenValue, String userMail);

  void deleteToken(String tokenValue);
}
