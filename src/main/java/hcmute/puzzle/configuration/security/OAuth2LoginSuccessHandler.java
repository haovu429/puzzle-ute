package hcmute.puzzle.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Autowired
  UserSecurityService userService;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
//    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//    String email = oAuth2User.getEmail();
//
//    System.out.println("Customer's email: " + email);
//
//    String token = userService.processOAuthPostLogin(oAuth2User.getEmail(), oAuth2User.getName());
//
//    System.out.println(token);
//
//    response.addHeader("Authorization", token);
//
//    ResponseObject responseObject = new ResponseObject("401", 200, token);
//    // System.out.println(authException.getMessage());
//    responseObject.setMessage("Invalid token");
//    response.setStatus(200);
//    response.setContentType("application/json");
//
//    ObjectMapper objectMapper = new ObjectMapper();
//    response.getWriter().write(objectMapper.writeValueAsString(responseObject));

    //super.onAuthenticationSuccess(request, response, authentication);
  }
}
