//package hcmute.puzzle.accessHandler;
//
//import com.abc.senki.model.entity.UserEntity;
//import com.abc.senki.security.jwt.JwtUtils;
//import com.abc.senki.service.UserService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//
//import java.util.UUID;
//
//import static com.google.common.net.HttpHeaders.AUTHORIZATION;
//
//@Component
//public class AuthenticationHandler {
//    @Autowired
//    JwtUtils jwtUtils;
//
//    @Autowired
//    UserService userService;
//
//    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
//    public UserEntity userAuthenticate(HttpServletRequest request){
//        String authorizationHeader = request.getHeader(AUTHORIZATION);
//        if(authorizationHeader != null && authorizationHeader.startsWith(BEARER_TOKEN_PREFIX)){
//            String accessToken = authorizationHeader.substring(BEARER_TOKEN_PREFIX.length());
//            if(jwtUtils.validateExpiredToken(accessToken)){
//                throw new BadCredentialsException("access token is  expired");
//            }
//            return userService.findById(UUID.fromString(jwtUtils.getUserNameFromJwtToken(accessToken)));
//        }
//        return null;
//    }
//}