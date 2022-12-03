package hcmute.puzzle.accessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.response.DataResponse;
import org.slf4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    ResponseObject responseObject =
        new ResponseObject("403", DataResponse.STATUS_FORBIDDEN, accessDeniedException.getMessage());

    if (auth != null) {
      LOG.warn(
          "User: "
              + auth.getName()
              + " access denied the protected URL: "
              + request.getRequestURI());
      responseObject.setMessage(
          "User: "
              + auth.getName()
              + " access denied the protected URL: "
              + request.getRequestURI());
    }

    response.setStatus(403);
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(responseObject));
  }
}
