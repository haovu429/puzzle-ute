package hcmute.puzzle.configuration.accessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.puzzle.exception.ErrorResponse;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static hcmute.puzzle.utils.Constant.ResponseCode.STATUS_FORBIDDEN;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  public static final Logger LOG =
      org.slf4j.LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
  // Jackson JSON serializer instance
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    DataResponse<String> responseObject =
        new DataResponse<>(ErrorResponse.UNAUTHORIZED_ERROR.getErrorCode().getValue(), accessDeniedException.getMessage(), HttpStatus.SC_OK);

    if (auth != null) {
      LOG.warn(
          "User: "
              + auth.getName()
              + " access denied the protected URL: "
              + request.getRequestURI());
      responseObject.setErrMsg(
          "User: "
              + auth.getName()
              + " access denied the protected URL: "
              + request.getRequestURI());
    }

    response.setStatus(HttpStatus.SC_FORBIDDEN);
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(responseObject));
  }

//  @Override
//  public void handle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
//          AccessDeniedException accessDeniedException) throws IOException, jakarta.servlet.ServletException {
//    
//  }
}
