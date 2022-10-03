package hcmute.puzzle.security;

public class TokenAuthenticationService {

  //    private RedisService service;
  //
  //    private long EXPIRATIONTIME = 1000 * 60 * 60; // 1 hr
  //
  //    private String secret;
  //
  //    private String tokenPrefix = "Bearer";
  //
  //    private String headerString = "Authorization";
  //
  //    public TokenAuthenticationService(RedisService service) {
  //        this.service = service;
  //        secret = Sha512DigestUtils.shaHex(System.getenv("ENC_KEY"));
  //    }
  //
  //    public void addAuthentication(HttpServletResponse response, AuthenticationTokenImpl auth) {
  //        // We generate a token now.
  //        Map<String, Object> claims = new HashMap <>();
  //        claims.put("username", auth.getPrincipal());
  //        claims.put("hash", auth.getHash());
  //        String JWT = Jwts.builder()
  //                .setSubject(auth.getPrincipal().toString())
  //                .setClaims(claims)
  //                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
  //                .signWith(SignatureAlgorithm.HS512, secret)
  //                .compact();
  //        response.addHeader(headerString, tokenPrefix + " " + JWT);
  //    }
  //
  //    public Authentication getAuthentication(HttpServletRequest request) {
  //        String token = request.getHeader(headerString);
  //        if (token == null) {
  //            return null;
  //        }
  //        //remove "Bearer" text
  //        token = token.replace(tokenPrefix, "").trim();
  //
  //        //Validating the token
  //        if (token != null && !token.isEmpty()) {
  //            // parsing the token.`
  //            Claims claims = null;
  //            try {
  //                claims = Jwts.parser()
  //                        .setSigningKey(secret)
  //                        .parseClaimsJws(token).getBody();
  //
  //            } catch ( Exception e) {
  //                return null;
  //            }
  //
  //            //Valid token and now checking to see if the token is actally expired or alive by
  // quering in redis.
  //            if (claims != null && claims.containsKey("username")) {
  //                String username = claims.get("username").toString();
  //                String hash = claims.get("hash").toString();
  //                SessionUser user = (SessionUser) service.getValue(String.format("%s:%s",
  // username,hash), SessionUser.class);
  //                if (user != null) {
  //                    AccessTokenBasedAuth auth = new AccessTokenBasedAuth(user.getUsername(),
  // Collections.emptyList());
  //                    auth.setDetails(user);
  //                    auth.authenticate();
  //                    return auth;
  //                } else {
  //                    return new UsernamePasswordAuthenticationToken(null, null);
  //                }
  //
  //            }
  //        }
  //        return null;
  //    }
}
