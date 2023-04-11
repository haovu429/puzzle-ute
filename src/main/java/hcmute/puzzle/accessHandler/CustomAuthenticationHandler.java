package hcmute.puzzle.accessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.response.DataResponse;
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
    DataResponse dataResponse = new DataResponse(DataResponse.CODE_ERROR_FORBIDDEN, authException.getMessage(), DataResponse.STATUS_BAD);
    ResponseObject responseObject = new ResponseObject("530", 530, authException.getMessage());
    // System.out.println(authException.getMessage());
    //responseObject.setMessage("Invalid token");
    //response.setStatus(401);
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(dataResponse));
  }
}
