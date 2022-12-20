package hcmute.puzzle.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.login_google.GooglePojo;
import hcmute.puzzle.login_google.GoogleUtils;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.security.JwtTokenProvider;
import hcmute.puzzle.security.UserService;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.RedisUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class AuthenticationController {
  @Autowired AuthenticationManager authenticationManager;

  @Autowired private JwtTokenProvider tokenProvider;

  @Autowired private UserRepository userRepository;

  @Autowired private RedisUtils redisUtils;

  @Autowired private GoogleUtils googleUtils;

  @Autowired private UserService userService;

  @PostMapping("/login")
  public ResponseObject authenticateUser(
      @Validated @RequestBody ObjectNode objectNode,
      HttpServletRequest req,
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

  @PostMapping("/login-google")
  public DataResponse loginGoogle(@RequestBody Map<String, Object> input)
      throws ClientProtocolException, IOException, GeneralSecurityException, NoSuchFieldException,
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

  @GetMapping("/logout")
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
}
