package hcmute.puzzle.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.security.CustomUserDetails;
import hcmute.puzzle.security.JwtTokenProvider;
import hcmute.puzzle.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(value = "http://localhost:3000")
public class AuthenticationController {
  @Autowired AuthenticationManager authenticationManager;

  @Autowired private JwtTokenProvider tokenProvider;

  @Autowired private UserRepository userRepository;

  @Autowired private RedisUtils redisUtils;

  @PostMapping("/login")
  public ResponseObject authenticateUser(
      @Validated @RequestBody ObjectNode objectNode,
      @RequestParam(value = "rememberMe", required = false) Boolean rememberMe) {
    try {
      // System.out.println("Da vao day");
      // String a = objectNode.get("email").toString();
      UserEntity user = userRepository.getByEmail(objectNode.get("email").asText());
      if (user != null) {
        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    objectNode.get("email").asText(), objectNode.get("password").asText()));
        //        Set in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

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

        Set<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());

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
