package hcmute.puzzle.controller;


import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class RoleController {

//	@Autowired
//	private RoleService roleService;

	@Autowired
	private RoleRepository roleRepository;

	@GetMapping("/role/code/{code}")
	ResponseObject getAll(@PathVariable String code) {
		ResponseObject reps = new ResponseObject(roleRepository.findOneByCode(code));
		return reps;
	}

	@GetMapping("/role/admin")
	ResponseObject admin() {
		return new ResponseObject("Hao dep trai");
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
