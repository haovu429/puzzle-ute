package hcmute.puzzle.controller;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.services.ApplicationService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class ApplicationController {

  @Autowired ApplicationService applicationService;

  @Autowired Converter converter;

  @GetMapping("/application/get-all")
  @ResponseBody
  public ResponseObject getAll(@RequestParam("page") Optional<Integer> page) {
    Pageable pageable = PageRequest.of(page.orElse(0), 5);
    return applicationService.findAll(pageable);
  }

  @GetMapping("/application/get-one")
  @ResponseBody
  public ResponseObject findById(@RequestParam("id") long id) throws CustomException {
    return applicationService.findById(id);
  }
}
