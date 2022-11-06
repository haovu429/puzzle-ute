package hcmute.puzzle.controller;

import hcmute.puzzle.test.SetUpDB;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class TestController {

  @Autowired SetUpDB setUpDB;

  @GetMapping("/init-db")
  public String getAll() {
    setUpDB.preStart();
    return "Done!";
  }
}
