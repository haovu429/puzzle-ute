package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.SystemInfo;
import hcmute.puzzle.entities.TokenEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.mail.MailObject;
import hcmute.puzzle.mail.SendMail;
import hcmute.puzzle.repository.TokenRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SecurityServiceImpl implements SecurityService {
  @Autowired private TokenRepository tokenRepository;

  @Autowired private UserRepository userRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired private Environment environment;

  public static final String RESET_PASSWORD_TOKEN = "RESET_PASSWORD_TOKEN";

  @Override
  public DataResponse sendTokenForgotPwd(HttpServletRequest request, String email) {
    UserEntity foundUser = userRepository.getUserByEmail(email);
    if (foundUser == null) {
      throw new RuntimeException("Can not found User Account with email " + email);
    }

    if (!foundUser.isEmailVerified()) {
      throw new CustomException("The account's email is not verified, try other ways or contact the Admin");
    }

    List<TokenEntity> existedTokens = tokenRepository.findTokensByUserAndType(foundUser.getId(), RESET_PASSWORD_TOKEN);

    if (!existedTokens.isEmpty()) {
      List<TokenEntity> validTokens =
          existedTokens.stream()
              .filter(token -> token.getExpiryTime().after(new Date()))
              .collect(Collectors.toList());
      if (validTokens == null || validTokens.size() > 0)
        throw new CustomException("You just requested change password, try again after 5 minutes");
    }
    String tokenValue = UUID.randomUUID().toString();
    Date nowTime = new Date();
    Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_RESET_PASSWORD_DURATION);

    TokenEntity createToken = new TokenEntity();
    createToken.setToken(tokenValue);
    createToken.setCreateTime(nowTime);
    createToken.setExpiryTime(expiredTime);
    createToken.setUser(foundUser);
    createToken.setType(RESET_PASSWORD_TOKEN);

    tokenRepository.save(createToken);
    String urlFrontEnd = "https://keen-semifreddo-66d931.netlify.app/reset-password";
    MailObject mail =
        new MailObject(
            foundUser.getEmail(),
            "Reset Password",
            "Link reset password: " + urlFrontEnd + "?token=" +
                tokenValue
                + "\nSupport email: "
                + environment.getProperty("support.email"), null);
    SendMail.sendMail(mail);

    return new DataResponse("send email reset password successful");
  }

  @Override
  public DataResponse resetPassword(String token, String newPassword) {
    TokenEntity foundToken = tokenRepository.findByToken(token);
    if (foundToken == null) {
      throw new CustomException("Token is invalid");
    }

    if (foundToken.getExpiryTime().before(new Date())) {
      throw new CustomException("Token was expired");
    }
    UserEntity user = tokenRepository.findUserByToken(token);
    if (user == null) {
      throw new CustomException("No user was found");
    }

    if (passwordEncoder.matches(newPassword, user.getPassword())) {
      throw new CustomException("You cannot reset the old password");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    tokenRepository.delete(foundToken);

    return new DataResponse("change password successful");
  }


}
