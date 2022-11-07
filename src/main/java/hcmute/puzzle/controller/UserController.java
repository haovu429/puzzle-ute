package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.services.UserService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class UserController {

  @Autowired public UserService userService;

  @Autowired
  JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  Converter converter;

  @GetMapping("/user")
  public ResponseObject getAll() {
    return userService.getAll();
  }

  @GetMapping("/user/profile")
  public ResponseObject getOne(@RequestHeader(value = "Authorization") String token) {
    Optional<UserEntity> linkUser = jwtAuthenticationFilter.getUserEntityFromToken(token);

    if (linkUser.isEmpty()) {
      throw new CustomException("Not found account");
    }

    UserDTO userDTO = converter.toDTO(linkUser.get());
    userDTO.setPassword(null);

    return new ResponseObject(200, "Profile info", userDTO);
  }

  @PostMapping("/user")
  public ResponseObject save(@ModelAttribute UserDTO user) {
    Set<String> roleCodes = new HashSet<>();
    roleCodes.add("user");

    user.setRoleCodes(roleCodes);
    return userService.save(user);
  }

  @DeleteMapping("/user/{id}")
  public ResponseObject delete(@PathVariable Long id) {
    return userService.delete(id);
  }

  @PutMapping("/user/{id}")
  public ResponseObject update(@PathVariable Long id, @RequestBody UserDTO user) {
    return userService.update(id, user);
  }

  @GetMapping("/test")
  public String update() {
    return "OK";
  }
}
