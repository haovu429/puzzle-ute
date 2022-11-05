package hcmute.puzzle.controller;

import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(value = "http://localhost:3000")
public class CompanyController {
  @Autowired public CompanyService companyService;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @GetMapping("/company")
  public ResponseObject getAll() {
    return companyService.getAll();
  }

  @DeleteMapping("/company/{id}")
  public ResponseObject delete(@PathVariable Long id) {
    return companyService.delete(id);
  }

  @PutMapping("/company/{id}")
  public ResponseObject update(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
    // return companyService.update(id, companyDTO);
    return null;
  }
}
