package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.entities.TokenEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
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
  public UserEntity findUSerByToken(String token) {
    return tokenRepository.findUserByToken(token);
  }

  @Override
  public void saveToken(String tokenValue, String userMail) {
    UserEntity user = userRepository.getByEmail(userMail).orElse(null);
    if (user == null) {
      throw new RuntimeException("User was not found by email: " + userMail);
    }
    TokenEntity tokenEntity = new TokenEntity();
    tokenEntity.setUser(user);
    tokenEntity.setToken(tokenValue);

    tokenRepository.save(tokenEntity);
  }

  @Override
  public void deleteToken(String tokenValue) {
    TokenEntity tokenEntity = tokenRepository.findByToken(tokenValue);
    if (tokenEntity == null) throw new RuntimeException("TokenEntity is not existed");
    tokenRepository.delete(tokenEntity);
  }
}
