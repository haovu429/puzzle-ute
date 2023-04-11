//package hcmute.puzzle.filter;
//
//import hcmute.puzzle.security.CustomAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//  public static final String SPRING_SECURITY_FORM_DOMAIN_KEY = "domain";
//
//  @Override
//  public Authentication attemptAuthentication(
//      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//
//    CustomAuthenticationToken authRequest = getAuthRequest(request);
//    setDetails(request, authRequest);
//    return this.getAuthenticationManager().authenticate(authRequest);
//  }
//
//  private CustomAuthenticationToken getAuthRequest(HttpServletRequest request) {
//    String username = obtainUsername(request);
//    String password = obtainPassword(request);
//    String domain = obtainDomain(request);
//
//    // ...
//
//    return new CustomAuthenticationToken(username, password, domain);
//  }
//}
