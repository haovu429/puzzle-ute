package hcmute.puzzle.controller;

import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
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

    // roleRepository.findAll();

    return new ResponseObject(result);
  }
  //
  //	@PostMapping("/role")
  //	ResponseObject save(@RequestBody RoleDto role) {
  //		return roleService.save(role);
  //
  //	}
  //
  //	@PutMapping("/role")
  //	ResponseObject update(@RequestBody RoleDto role) {
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
