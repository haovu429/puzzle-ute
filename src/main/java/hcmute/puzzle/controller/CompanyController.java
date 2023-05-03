package hcmute.puzzle.controller;

import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.services.CompanyService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class CompanyController {
  @Autowired public CompanyService companyService;

  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;




}
