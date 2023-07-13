package hcmute.puzzle.interceptor;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CommonInterceptor implements HandlerInterceptor {

  Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
    logger.info("Pre Handle method is Calling. End point: {} - Method: {} - By {}", request.getRequestURI(),
                request.getMethod(), currentUserName);
    logger.info("With parameters: ");
    request.getParameterMap().forEach((key, value) -> {
      logger.info("key: {} , value: {}", key, value);
    });
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {

    logger.info("Post Handle method is Calling. Response status: {}", response.getStatus());
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception)
      throws Exception {

    logger.info("Request and Response is completed");
  }
}
