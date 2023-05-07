package hcmute.puzzle.services.impl;

import freemarker.template.TemplateException;
import hcmute.puzzle.configuration.SystemInfo;
import hcmute.puzzle.infrastructure.entities.TokenEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.TokenRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

import hcmute.puzzle.services.SecurityService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import hcmute.puzzle.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;

    public static final String RESET_PASSWORD_TOKEN = "RESET_PASSWORD_TOKEN";

    public static final String VERIFY_ACCOUNT_TOKEN = "VERIFY_ACCOUNT_TOKEN";

    @Override
    public DataResponse sendTokenForgotPwd(HttpServletRequest request, String email)
            throws MessagingException,
            TemplateException,
            IOException,
            ExecutionException,
            InterruptedException {
        UserEntity foundUser = userRepository.getUserByEmail(email);
        if (foundUser == null) {
            throw new RuntimeException("Can not found User Account with email " + email);
        }

        if (!foundUser.isEmailVerified()) {
            throw new CustomException(
                    "The account's email is not verified, try other ways or contact the Admin");
        }

        List<TokenEntity> existedTokens =
                tokenRepository.findTokensByUserAndType(foundUser.getId(), RESET_PASSWORD_TOKEN);

        if (!existedTokens.isEmpty()) {
            List<TokenEntity> validTokens =
                    existedTokens.stream().filter(token -> token.getExpiryTime().after(new Date())).collect(Collectors.toList());
            if (!validTokens.isEmpty())
                throw new CustomException("You just requested change password, try again after 1 minutes");
        }
        String tokenValue = UUID.randomUUID().toString();
        Date nowTime = new Date();
        Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_RESET_PASSWORD_DURATION);

        TokenEntity createToken = new TokenEntity();
        createToken.setToken(tokenValue);
        createToken.setExpiryTime(expiredTime);
        createToken.setUser(foundUser);
        createToken.setType(RESET_PASSWORD_TOKEN);

        String urlFrontEnd = "https://keen-semifreddo-66d931.netlify.app/reset-password";
        String finalUrlFrontEnd = urlFrontEnd + "?token=" + tokenValue;
        this.userService.sendMailForgotPwd(foundUser.getEmail(), finalUrlFrontEnd, createToken);

        log.info("Responseeeeeeeeeeee");
        return new DataResponse("Mail is sent, please check!");
    }

    @Override
    public DataResponse sendTokenVerifyAccount(String email)
            throws MessagingException,
                   TemplateException,
                   IOException,
                   ExecutionException,
                   InterruptedException {
        UserEntity foundUser = userRepository.getUserByEmail(email);
        if (foundUser == null) {
            throw new RuntimeException("Can not found User Account with email " + email);
        }

        if (foundUser.isEmailVerified()) {
            throw new CustomException(
                    "The account's email is verified, No need to repeat");
        }

        String tokenValue = UUID.randomUUID().toString();
        Date nowTime = new Date();
        Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_VERIFY_ACCOUNT_DURATION);

        TokenEntity createToken = new TokenEntity();
        createToken.setToken(tokenValue);
        createToken.setExpiryTime(expiredTime);
        createToken.setUser(foundUser);
        createToken.setType(VERIFY_ACCOUNT_TOKEN);

        String urlFrontEnd = "https://keen-semifreddo-66d931.netlify.app/verify_account";
        String finalUrlFrontEnd = urlFrontEnd + "?token=" + tokenValue;
        this.userService.sendMailVerifyAccount(foundUser.getEmail(), finalUrlFrontEnd, createToken);

        return new DataResponse("Mail verify account is sent, please check!");
    }


    //  private TokenEntity createTokenForgotPassword(UserEntity foundUser) {
    //    String tokenValue = UUID.randomUUID().toString();
    //    Date nowTime = new Date();
    //    Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_RESET_PASSWORD_DURATION);
    //
    //    TokenEntity createToken = new TokenEntity();
    //    createToken.setToken(tokenValue);
    //    createToken.setCreateTime(nowTime);
    //    createToken.setExpiryTime(expiredTime);
    //    createToken.setUser(foundUser);
    //    createToken.setType(RESET_PASSWORD_TOKEN);
    //
    //    return tokenRepository.save(createToken);
    //  }


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
