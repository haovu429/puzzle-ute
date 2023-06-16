package hcmute.puzzle.configuration.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

  // Đoạn JWT_SECRET này là bí mật, chỉ có phía server biết
  @Value("${jwt.secret}")
  private String JWT_SECRET; // = "puzzle-ute";

  // Thời gian có hiệu lực của chuỗi jwt
  // private final long JWT_EXPIRATION = 604800000L;

  // Tạo ra jwt từ thông tin user
  public String generateToken(CustomUserDetails userDetails, long JWT_EXPIRATION) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
    //byte[] bytes = TextCodec.BASE64.decode(JWT_SECRET.getBytes().toString());
    //System.out.println(bytes);
    //System.out.println(JWT_SECRET.getBytes());

    // Tạo chuỗi json web token từ id của user.
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(now)
        .claim("roles", userDetails.getAuthorities())
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
        .compact();
  }

  // Lấy thông tin user từ jwt
  public String getEmailFromJWT(String token) {
    Claims claims =
        Jwts.parser().setSigningKey(JWT_SECRET.getBytes()).parseClaimsJws(token).getBody();

    return String.valueOf(claims.getSubject());
  }

  // https://stackoverflow.com/questions/56639392/jwt-signature-does-not-match-locally-computed-signature-jwt-validity-cannot-be
  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(JWT_SECRET.getBytes()).parseClaimsJws(authToken);
      return true;
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty.");
    } catch (SignatureException ex) {
      log.error("JWT signature does not match locally computed signature.");
    }
    //        if (!error.equals("")) {
    //            throw new RuntimeException(error);
    //        }
    return false;
  }
}
