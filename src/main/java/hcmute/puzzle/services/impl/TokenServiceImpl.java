package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.entities.Token;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.infrastructure.repository.TokenRepository;
import hcmute.puzzle.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
  @Autowired TokenRepository tokenRepository;

  @Autowired UserRepository userRepository;

  @Override
  public User findUSerByToken(String token) {
    return tokenRepository.findUserByToken(token);
  }

  @Override
  public void saveToken(String tokenValue, String userMail) {
    User user = userRepository.getByEmail(userMail).orElse(null);
    if (user == null) {
      throw new RuntimeException("User was not found by email: " + userMail);
    }
    Token token = new Token();
    token.setEmail(user.getEmail());
    token.setToken(tokenValue);

    tokenRepository.save(token);
  }

  @Override
  public void deleteToken(String tokenValue) {
    Token token = tokenRepository.findByToken(tokenValue);
    if (token == null) throw new RuntimeException("TokenEntity is not existed");
    tokenRepository.delete(token);
  }
}
