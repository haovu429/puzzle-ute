package hcmute.puzzle.services;

//import javax.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestService {
  String getClientIp(HttpServletRequest request);
}
