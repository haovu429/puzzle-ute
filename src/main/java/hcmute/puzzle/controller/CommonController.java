package hcmute.puzzle.controller;

import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.repository.CandidateRepository;
import hcmute.puzzle.repository.JobPostRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.CandidateService;
import hcmute.puzzle.services.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class CommonController {

    @Autowired
    CandidateService candidateService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobPostService jobPostService;

    @Autowired
    CandidateRepository candidateRepository;
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    ApplicationRepository applicationRepository;

    @GetMapping("/job-post")
    ResponseObject getById() {
        return jobPostService.getAll();
    }
}
