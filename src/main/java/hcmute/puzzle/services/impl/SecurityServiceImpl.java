package hcmute.puzzle.services.impl;

import freemarker.template.TemplateException;
import hcmute.puzzle.configuration.SystemInfo;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.Token;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.repository.TokenRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.SecurityService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    public static final String RESET_PASSWORD_TOKEN = "RESET_PASSWORD_TOKEN";

    public static final String VERIFY_ACCOUNT_TOKEN = "VERIFY_ACCOUNT_TOKEN";

    @Override
    public DataResponse sendTokenForgotPwd(HttpServletRequest request, String email)
            throws MessagingException,
            TemplateException,
            IOException,
            ExecutionException,
            InterruptedException {
        User foundUser = userRepository.getUserByEmail(email);
        if (foundUser == null) {
            throw new NotFoundDataException("Can not found User Account with email " + email);
        }

        if (!foundUser.getEmailVerified()) {
            throw new UnauthorizedException(
                    "The account's email is not verified, try other ways or contact the Admin");
        }

        List<Token> existedTokens =
                tokenRepository.findTokensByUserAndType(foundUser.getId(), RESET_PASSWORD_TOKEN);

        if (!existedTokens.isEmpty()) {
            List<Token> validTokens =
                    existedTokens.stream().filter(token -> token.getExpiryTime().after(new Date())).collect(Collectors.toList());
            if (!validTokens.isEmpty())
                throw new CustomException("You just requested change password, try again after 1 minutes");
        }
        String tokenValue = UUID.randomUUID().toString();
        Date nowTime = new Date();
        Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_RESET_PASSWORD_DURATION);

        Token createToken = new Token();
        createToken.setToken(tokenValue);
        createToken.setExpiryTime(expiredTime);
        createToken.setUser(foundUser);
        createToken.setType(RESET_PASSWORD_TOKEN);

        String urlFrontEnd = "https://keen-semifreddo-66d931.netlify.app/reset-password";
        String finalUrlFrontEnd = urlFrontEnd + "?token=" + tokenValue;
        this.sendMailForgotPwd(foundUser.getEmail(), finalUrlFrontEnd, createToken);

        log.info("Responseeeeeeeeeeee");
        return new DataResponse("Mail is sent, please check!");
    }

    @Async
    public void sendMailForgotPwd(String receiveMail, String urlResetPass, Token token) throws InterruptedException,
            MessagingException, TemplateException, IOException, ExecutionException {
        //MailService mailService = new MailService();
        mailService.executeSendMailWithThread(receiveMail, urlResetPass, token);
    }

    @Override
    public DataResponse sendTokenVerifyAccount(String email) throws MessagingException, TemplateException, IOException,
            ExecutionException, InterruptedException {
        User foundUser = userRepository.findByEmail(email)
                                       .orElseThrow(() -> new NotFoundDataException(
                                               "Can not found User Account with email " + email));
        if (foundUser.getEmailVerified() == null || foundUser.getEmailVerified()) {
            throw new UnauthorizedException("The account's email is verified, No need to repeat");
        }

        String tokenValue = UUID.randomUUID().toString();
        Date nowTime = new Date();
        Date expiredTime = new Date(nowTime.getTime() + SystemInfo.TOKEN_VERIFY_ACCOUNT_DURATION);

        Token createToken = new Token();
        createToken.setToken(tokenValue);
        createToken.setExpiryTime(expiredTime);
        createToken.setUser(foundUser);
        createToken.setType(VERIFY_ACCOUNT_TOKEN);

        String urlFrontEnd = "https://keen-semifreddo-66d931.netlify.app/verify_account";
        String finalUrlFrontEnd = urlFrontEnd + "?token=" + tokenValue;
        this.sendMailVerifyAccount(foundUser.getEmail(), finalUrlFrontEnd, createToken);

        return new DataResponse("Mail verify account is sent, please check!");
    }

    @Async
    public void sendMailVerifyAccount(String receiveMail, String verifyUrl, Token token) throws InterruptedException,
            MessagingException, TemplateException, IOException, ExecutionException {
        //MailService mailService = new MailService();
        mailService.executeSendMailVerifyAccountWithThread(receiveMail, verifyUrl, token);
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
        Token foundToken = tokenRepository.findByToken(token);
        if (foundToken == null) {
            throw new CustomException("Token is invalid");
        }

        if (foundToken.getExpiryTime().before(new Date())) {
            throw new CustomException("Token was expired");
        }
        User user = tokenRepository.findUserByToken(token);
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

    @Override
    public DataResponse verifyAccount(String token) {
        Token foundToken = tokenRepository.findByToken(token);
        if (foundToken == null) {
            throw new CustomException("Token is invalid");
        }

        if (foundToken.getExpiryTime().before(new Date())) {
            throw new CustomException("Token was expired");
        }
        User user = tokenRepository.findUserByToken(token);
        if (user == null) {
            throw new CustomException("No user was found");
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(foundToken);

        return new DataResponse("Account is verified");
    }
}
