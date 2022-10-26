package hcmute.puzzle.controller;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class RoleController {

  //	@Autowired
  //	private RoleService roleService;

  @Autowired private RoleRepository roleRepository;

  @GetMapping("/role/code/{code}")
  ResponseObject getAll(@PathVariable String code) {
    ResponseObject reps = new ResponseObject(roleRepository.findOneByCode(code));
    return reps;
  }

  @GetMapping("/role/admin")
  ResponseObject admin() {
    String result = "false";
    Integer a = 1000;
    int b = 1000;
    if (a == b) {
      result = "true";
    }

    //roleRepository.findAll();

    return new ResponseObject(result);
  }
  //
  //	@PostMapping("/role")
  //	ResponseObject save(@RequestBody RoleDTO role) {
  //		return roleService.save(role);
  //
  //	}
  //
  //	@PutMapping("/role")
  //	ResponseObject update(@RequestBody RoleDTO role) {
  //		return roleService.update(role);
  //
  //	}
  //
  //	@DeleteMapping("/role/{code}")
  //	ResponseObject delete(@PathVariable String code) {
  //		return roleService.delete(code);
  //
  //	}

}
