package hcmute.puzzle.controller;

import freemarker.template.TemplateException;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.configuration.security.JwtTokenProvider;
import hcmute.puzzle.configuration.security.UserSecurityService;
import hcmute.puzzle.exception.ErrorDefine;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.request.LoginRequest;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.SecurityService;
import hcmute.puzzle.services.UserService;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.RedisUtils;
import hcmute.puzzle.utils.login_google.GooglePojo;
import hcmute.puzzle.utils.login_google.GoogleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
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
  private UserRepository userRepository;

  @Autowired
  private RedisUtils redisUtils;

  @Autowired
  private GoogleUtils googleUtils;

  @Autowired
  private UserSecurityService userSecurityService;

  @Autowired
  private UserService userService;

  @Autowired
  private SecurityService securityService;

  //  @Transactional
  @PostMapping(LOGIN_URL)
  public ResponseObject<Object> authenticateUser(@Validated @RequestBody LoginRequest loginRequest,
          @RequestParam(value = "rememberMe", required = false) Boolean rememberMe) {
    try {

      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

      //      String email = loginRequest.getEmail();
      //      UserEntity user = userRepository.getByEmail(email)
      //                                      .orElseThrow(() -> new NotFoundDataException("EMAIL ISN'T EXISTS"));
      CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
      User user = customUserDetails.getUser();
      if (!user.isActive()) {
        throw new UnauthorizedException("THIS ACCOUNT ISN'T ACTIVE");
      }

      if (!user.isEmailVerified()) {
        throw new UnauthorizedException("THIS ACCOUNT ISN'T VERIFY");
      }

      Long JWT_EXPIRATION = (long) (60 * 60 * 24 * 1000); // 1 day
      if (rememberMe != null) {
        JWT_EXPIRATION *= 7; // 7 days
      }
      // get jwt token
      String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal(), JWT_EXPIRATION);

      // store token in redis
      redisUtils.set(user.getEmail(), jwt);

      Set<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());

      Map<String, Object> result = new HashMap<>();
      result.put("jwt", jwt);
      result.put("roles", roles);
      user.setLastLoginAt(new Date());
      //userService.prepareForRole(user);

      return new ResponseObject<>(200, "Login success", result);

    } catch (BadCredentialsException e) {
      log.error(e.getMessage(), e);
      return new ResponseObject<>("Email or password incorrect!");
    } catch (LockedException e) {
      log.error(e.getMessage(), e);
      return new ResponseObject<>(ErrorDefine.ClientError.AUTHENTICATION_ERROR, HttpStatus.UNAUTHORIZED.value(),
                                  e.getMessage());
    }
  }

  @PostMapping(LOGIN_GOOGLE_URL)
  public DataResponse<Map<String, Object>> loginGoogle(@RequestBody Map<String, Object> input) throws IOException,
          GeneralSecurityException, NoSuchFieldException, IllegalAccessException {
    //    String code = request.getParameter("code");
    //
    //    if (code == null || code.isEmpty()) {
    //      return "redirect:/login?google=error";
    //    }
    //    String accessToken = googleUtils.getToken(code);
    String token = String.valueOf(input.get("token"));
    GooglePojo googlePojo = googleUtils.getUserInfoFromCredential(token);
    return new DataResponse<>(userSecurityService.processOAuthPostLogin(googlePojo));
    // UserDetails userDetail = googleUtils.buildUser(googlePojo);
    //    UsernamePasswordAuthenticationToken authentication =
    //        new UsernamePasswordAuthenticationToken(userDetail, null,
    // userDetail.getAuthorities());
    //    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    //    SecurityContextHolder.getContext().setAuthentication(authentication);
    // return "redirect:/user";

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
  public DataResponse verifyAccount(@RequestBody Map<String, String> data) {
    String token = data.get("token");
    String email = data.get("email");
    if (Objects.isNull(token) || Objects.isNull(email)) {
      String mess = String.format("Miss information: token: %s, email: %s", token, email);
      log.warn("Miss information: token: {}, email: {}", token, email);
      return new DataResponse(ErrorDefine.ClientError.BAD_REQUEST_ERROR, mess, ErrorDefine.CLIENT_ERROR_CODE);
    }
    return securityService.verifyAccount(token, email);
  }
}
