package hcmute.puzzle.services.Impl;


import hcmute.puzzle.entities.Token;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.TokenRepository;
import hcmute.puzzle.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserEntity findUSerByToken(String token){
        return tokenRepository.findUserByToken(token);
    }

    @Override
    public void saveToken(String tokenValue, String userMail){
        UserEntity user = userRepository.findByEmail(userMail);
        if(user == null){
            throw new RuntimeException("User was not found by email: " + userMail);
        }
        Token token = new Token();
        token.setUser(user);
        token.setToken(tokenValue);
        token.setCreateDate(new Date());

        tokenRepository.save(token);
    }

    @Override
    public void deleteToken(String tokenValue){
        Token token = tokenRepository.findByToken(tokenValue);
        if(token == null)
            throw new RuntimeException("Token is not existed");
        tokenRepository.delete(token);
    }
}
