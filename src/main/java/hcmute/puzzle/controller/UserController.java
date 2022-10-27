package hcmute.puzzle.controller;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;
import hcmute.puzzle.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping(path = "/api")
@CrossOrigin(value = "http://localhost:3000")
public class UserController {

  @Autowired public UserService userService;

  @GetMapping("/user")
  public ResponseObject getAll() {
    return userService.getAll();
  }

  @PostMapping("/user")
  public ResponseObject save(@RequestBody UserDTO user) {
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
