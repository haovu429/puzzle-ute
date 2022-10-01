package hcmute.puzzle.services;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


public interface RequestService {
    String getClientIp(HttpServletRequest request);
}
