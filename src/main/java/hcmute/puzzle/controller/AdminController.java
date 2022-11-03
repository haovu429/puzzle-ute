package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.CompanyService;
import hcmute.puzzle.services.EmployerService;
import hcmute.puzzle.services.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class AdminController {

  @Autowired EmployerService employerService;

  @Autowired UserRepository userRepository;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired JobPostRepository jobPostRepository;

  @Autowired JobPostService jobPostService;

  @Autowired Converter converter;

  @Autowired CompanyService companyService;

  @PostMapping("/admin/create-info-company")
  public ResponseObject createCompany(@RequestBody CompanyDTO companyDTO) {
    return companyService.save(companyDTO);
  }

  @PutMapping("/admin/update-info-company")
  public ResponseObject updateCompany(@RequestBody CompanyDTO companyDTO) {
    return companyService.update(companyDTO);
  }

  @GetMapping("/admin/delete-info-company/{id}")
  public ResponseObject deleteCompany(@PathVariable Long id) {
    return companyService.delete(id);
  }
}
