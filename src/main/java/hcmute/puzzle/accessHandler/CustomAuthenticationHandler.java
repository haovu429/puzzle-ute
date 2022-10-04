package hcmute.puzzle.accessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.puzzle.dto.ResponseObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationHandler implements AuthenticationEntryPoint {

  // Jackson JSON serializer instance
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    ResponseObject responseObject = new ResponseObject("401", 200, authException.getMessage());
    // System.out.println(authException.getMessage());
    responseObject.setMessage("Invalid token");
    response.setStatus(200);
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(responseObject));
  }
}
