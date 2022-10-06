package hcmute.puzzle.filter;

import hcmute.puzzle.security.JwtTokenProvider;
import hcmute.puzzle.security.UserService;
import hcmute.puzzle.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
// @Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  @Autowired private JwtTokenProvider tokenProvider;

  @Autowired private UserService customUserDetailsService;

  @Autowired private RedisUtils redisUtils;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    //    response.setHeader("Access-Control-Allow-Origin", "*");
    //    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
    //    response.setHeader("Access-Control-Max-Age", "20000");
    //    response.setHeader(
    //        "Access-Control-Allow-Headers",
    //        "x-requested-with, authorization, Content-Type, Authorization, credential,
    // X-XSRF-TOKEN");
    //
    //    request.setAttribute("Access-Control-Allow-Origin", "*");
    //    request.setAttribute("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
    //    request.setAttribute("Access-Control-Max-Age", "20000");
    //    request.setAttribute(
    //            "Access-Control-Allow-Headers",
    //            "x-requested-with, authorization, Content-Type, Authorization, credential,
    // X-XSRF-TOKEN");
    try {
      // get request token from header
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        // get email from token
        String email = tokenProvider.getEmailFromJWT(jwt);
        // get user from email
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        // get token from redis
        String token = redisUtils.get(email) == null ? "" : redisUtils.get(email).toString();
        // System.out.println("Dung? :" + token.equals(jwt));
        if (userDetails != null && token.equals(jwt)) {
          // set authentication to context
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          System.out.println("Truot");
        }
      }
    } catch (AccessDeniedException ex) {
      throw new AccessDeniedException("Access Denied");
    } catch (Exception exception) {
      exception.printStackTrace();
      log.error("failed on set user authentication", exception);
    }

    filterChain.doFilter(request, response);
  }

  public String checkToken(String tokenFromHeader) {
    String validUserEmail = "";
    try {
      // get request token from header
      String jwt = tokenPreProcessing(tokenFromHeader);

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        // get email from token
        String email = tokenProvider.getEmailFromJWT(jwt);
        // get user from email
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        // get token from redis
        String token = redisUtils.get(email) == null ? "" : redisUtils.get(email).toString();
        // System.out.println("Dung? :" + token.equals(jwt));
        if (userDetails != null && token.equals(jwt)) {
          // pass
          validUserEmail = userDetails.getUsername();
        } else {
          throw new RuntimeException("Loi xac thuc token");
        }
      }
    } catch (AccessDeniedException ex) {
      throw new AccessDeniedException("Access Denied");
    } catch (Exception exception) {
      exception.printStackTrace();
      log.error("failed on set user authentication", exception);
      throw new RuntimeException("Loi xac thuc token");
    }
    return validUserEmail;
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    return tokenPreProcessing(bearerToken);
  }

  private String tokenPreProcessing(String bearerToken) {
    // Check if bearer token is valid
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
