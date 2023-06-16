package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.entities.User;

public interface TokenService {
  User findUSerByToken(String token);

  void saveToken(String tokenValue, String userMail);

  void deleteToken(String tokenValue);
}
