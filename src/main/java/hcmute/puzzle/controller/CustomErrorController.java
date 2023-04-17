package hcmute.puzzle.controller;

import hcmute.puzzle.utils.Constant;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin(origins = {Constant.LOCAL_URL, Constant.ONLINE_URL})
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  @ResponseBody
  String error(HttpServletRequest request) {
    return "<h1>ErrorDefine occurred</h1>";
  }
}
