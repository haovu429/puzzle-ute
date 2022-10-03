package hcmute.puzzle.controller;

import hcmute.puzzle.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
// @RequestMapping(path = "/api")
public class HomeController {
  @Autowired private RequestService requestService;

  @RequestMapping(method = RequestMethod.GET, value = "/ip", produces = MediaType.TEXT_PLAIN_VALUE)
  @ResponseBody
  public String index(HttpServletRequest request) {
    String clientIp = requestService.getClientIp(request);
    return clientIp;
  }
}
