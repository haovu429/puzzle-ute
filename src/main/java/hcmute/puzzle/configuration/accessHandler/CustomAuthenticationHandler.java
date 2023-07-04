package hcmute.puzzle.configuration.accessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.puzzle.exception.ErrorResponse;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

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
    DataResponse<String> dataResponse = new DataResponse<>(ErrorResponse.UNAUTHORIZED_ERROR.getErrorCode().getValue(),
                                                           authException.getMessage(), HttpStatus.SC_UNAUTHORIZED);
    // System.out.println(authException.getMessage());
    //responseObject.setMessage("Invalid token");
    //response.setStatus(401);
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(dataResponse));
  }
}
