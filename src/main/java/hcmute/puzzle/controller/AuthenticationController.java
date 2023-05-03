package hcmute.puzzle.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import freemarker.template.TemplateException;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.utils.login_google.GooglePojo;
import hcmute.puzzle.utils.login_google.GoogleUtils;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.configuration.security.JwtTokenProvider;
import hcmute.puzzle.configuration.security.UserService;
import hcmute.puzzle.services.SecurityService;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.RedisUtils;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class AuthenticationController {
  @Autowired AuthenticationManager authenticationManager;

  @Autowired private JwtTokenProvider tokenProvider;

  @Autowired private UserRepository userRepository;

  @Autowired private RedisUtils redisUtils;

  @Autowired private GoogleUtils googleUtils;

  @Autowired private UserService userService;

  @Autowired private SecurityService securityService;

  public static final String LOGIN_URL = "/login";

  public static final String LOGOUT_URL = "/logout";

  public static final String LOGIN_GOOGLE_URL = "/login-google";

  public static final String FORGOT_PASSWORD_URL = "/forgot-password";
  public static final String RESET_PASSWORD_URL = "/reset-password";

  @PostMapping(LOGIN_URL)
  public ResponseObject authenticateUser(
      @Validated @RequestBody ObjectNode objectNode,
      @RequestParam(value = "rememberMe", required = false) Boolean rememberMe) {
    try {
      // String a = objectNode.get("email").toString();
      UserEntity user = userRepository.getByEmail(objectNode.get("email").asText());
      if (user != null) {
        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    objectNode.get("email").asText(), objectNode.get("password").asText()));
        //        Set in security context
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        //        SecurityContext sc = SecurityContextHolder.getContext();
        //        sc.setAuthentication(authentication);
        //        HttpSession session = req.getSession(true);
        //        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        Long JWT_EXPIRATION = (long) (60 * 60 * 24 * 1000); // 1 day
        if (rememberMe != null) {
          JWT_EXPIRATION *= 7; // 7 days
        }
        // get jwt token
        String jwt =
            tokenProvider.generateToken(
                (CustomUserDetails) authentication.getPrincipal(), JWT_EXPIRATION);

        // store token in redis
        redisUtils.set(user.getEmail(), jwt);

        Set<String> roles =
            user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());

        Map<String, Object> result = new HashMap<>();
        result.put("jwt", jwt);
        result.put("roles", roles);

        return new ResponseObject(200, "Login success", result);

      } else {
        return new ResponseObject("401", 200, "Login failed");
      }
    } catch (BadCredentialsException e) {
      throw new RuntimeException("Invalid username/password supplied");
    }
  }

  @PostMapping(LOGIN_GOOGLE_URL)
  public DataResponse loginGoogle(@RequestBody Map<String, Object> input)
      throws ClientProtocolException,
          IOException,
          GeneralSecurityException,
          NoSuchFieldException,
          IllegalAccessException {
    //    String code = request.getParameter("code");
    //
    //    if (code == null || code.isEmpty()) {
    //      return "redirect:/login?google=error";
    //    }
    //    String accessToken = googleUtils.getToken(code);
    String token = String.valueOf(input.get("token"));
    GooglePojo googlePojo = googleUtils.getUserInfoFromCredential(token);
    return new DataResponse(userService.processOAuthPostLogin(googlePojo));
    // UserDetails userDetail = googleUtils.buildUser(googlePojo);
    //    UsernamePasswordAuthenticationToken authentication =
    //        new UsernamePasswordAuthenticationToken(userDetail, null,
    // userDetail.getAuthorities());
    //    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    //    SecurityContextHolder.getContext().setAuthentication(authentication);
    // return "redirect:/user";

  }

  @GetMapping(LOGOUT_URL)
  public ResponseObject logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object user = authentication.getPrincipal();
    if (user instanceof CustomUserDetails) {
      // delete token in redis
      redisUtils.delete(((CustomUserDetails) user).getUsername());
      return new ResponseObject(200, "Logout success", "");
    }
    return new ResponseObject("401", 200, "Logout fail");
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
}
