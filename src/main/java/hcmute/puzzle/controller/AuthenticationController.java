package hcmute.puzzle.controller;

import freemarker.template.TemplateException;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.configuration.security.JwtTokenProvider;
import hcmute.puzzle.configuration.security.UserSecurityService;
import hcmute.puzzle.exception.AuthenticationException;
import hcmute.puzzle.exception.ErrorDefine;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.LoginRequest;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.services.impl.SecurityService;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.RedisUtils;
import hcmute.puzzle.utils.login_google.GooglePojo;
import hcmute.puzzle.utils.login_google.GoogleUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static hcmute.puzzle.utils.Constant.AuthPath.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
@Slf4j
public class AuthenticationController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Autowired
  private RedisUtils redisUtils;

  @Autowired
  private GoogleUtils googleUtils;

  @Autowired
  private UserSecurityService userSecurityService;

  @Autowired
  private SecurityService securityService;

  //  @Transactional
  @PostMapping(LOGIN_URL)
  public DataResponse<Object> authenticateUser(@Validated @RequestBody LoginRequest loginRequest,
          @RequestParam(value = "rememberMe", required = false) Boolean rememberMe) {
    try {

      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

      //      String email = loginRequest.getEmail();
      //      UserEntity user = userRepository.getByEmail(email)
      //                                      .orElseThrow(() -> new NotFoundDataException("EMAIL ISN'T EXISTS"));
      CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
      User user = customUserDetails.getUser();
      if (!user.getIsActive()) {
        throw new UnauthorizedException("THIS ACCOUNT ISN'T ACTIVE");
      }

      if (!user.getEmailVerified()) {
        throw new UnauthorizedException("THIS ACCOUNT ISN'T VERIFY");
      }

      long JWT_EXPIRATION = (long) (60 * 60 * 24 * 1000); // 1 day
      if (rememberMe != null) {
        JWT_EXPIRATION *= 7; // 7 days
      }
      // get jwt token
      String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal(), JWT_EXPIRATION);

      // store token in redis
      redisUtils.set(user.getEmail(), jwt);

      Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

      Map<String, Object> result = new HashMap<>();
      result.put("jwt", jwt);
      result.put("roles", roles);
      user.setLastLoginAt(new Date());
      //userService.prepareForRole(user);
      return new DataResponse<>(result);
    } catch (BadCredentialsException e) {
      log.error(e.getMessage(), e);
      throw new AuthenticationException("Email or password incorrect!");
    } catch (LockedException e) {
      log.error(e.getMessage(), e);
      throw new AuthenticationException("Account inactive, check mail please");
    }
  }

  @PostMapping(LOGIN_GOOGLE_URL)
  public DataResponse<Map<String, Object>> loginGoogle(@RequestBody Map<String, Object> input) throws IOException,
          GeneralSecurityException, NoSuchFieldException, IllegalAccessException {
    String token = String.valueOf(input.get("token"));
    GooglePojo googlePojo = googleUtils.getUserInfoFromCredential(token);
    return new DataResponse<>(userSecurityService.processOAuthPostLogin(googlePojo));


  }

  @GetMapping(LOGOUT_URL)
  public ResponseObject<String> logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object user = authentication.getPrincipal();
    if (user instanceof CustomUserDetails) {
      // delete token in redis
      redisUtils.delete(((CustomUserDetails) user).getUsername());
      return new ResponseObject<>(200, "Logout success", "");
    }
    return new ResponseObject<>("401", 200, "Logout fail");
  }

  @GetMapping(FORGOT_PASSWORD_URL)
  public DataResponse forgotPassword(
      HttpServletRequest request, @RequestParam(value = "email") String email)
      throws MessagingException, TemplateException, IOException, ExecutionException, InterruptedException {
    System.out.println(request.getServletPath());

    return securityService.sendTokenForgotPwd(request, email);
  }

  @GetMapping(RESET_PASSWORD_URL)
  public DataResponse resetPassword(
      @RequestParam(value = "token") String token,
      @RequestParam(value = "newPassword") String newPassword) {
    return securityService.resetPassword(token, newPassword);
  }

  @GetMapping(RESEND_VERIFY_TOKEN_URL)
  public DataResponse resendMailVerifyAccount(@RequestParam(value = "email") String email)
          throws MessagingException, TemplateException, IOException, ExecutionException, InterruptedException {
    return securityService.sendTokenVerifyAccount(email);
  }

  @PutMapping(VERIFY_ACCOUNT_URL)
  public DataResponse verifyAccount(@RequestBody Map<String, String> data) throws MessagingException, TemplateException,
          IOException, ExecutionException, InterruptedException {
    String token = data.get("token");
    //    String email = data.get("email");
    if (Objects.isNull(token)) {
      String mess = String.format("Miss information: token: %s", token);
      log.warn("Miss information: token: {}, email: {}", token);
      return new DataResponse(ErrorDefine.ClientError.BAD_REQUEST_ERROR, mess, ErrorDefine.CLIENT_ERROR_CODE);
    }
    return securityService.verifyAccount(token);
  }
}
