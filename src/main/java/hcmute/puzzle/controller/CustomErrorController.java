package hcmute.puzzle.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin(value = "http://localhost:3000")
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  @ResponseBody
  String error(HttpServletRequest request) {
    return "<h1>Error occurred</h1>";
  }
}
