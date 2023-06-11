package hcmute.puzzle.utils.login_google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.configuration.security.UserSecurityService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.fluent.Form;
//import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class GoogleUtils {
  @Autowired private Environment env;

  @Autowired private UserSecurityService userService;

//  public String getToken(final String code) throws ClientProtocolException, IOException {
//    String link = env.getProperty("google.link.get.token");
//    String response =
//        Request.Post(link)
//            .bodyForm(
//                Form.form()
//                    .add("client_id", env.getProperty("google.app.id"))
//                    .add("client_secret", env.getProperty("google.app.secret"))
//                    .add("redirect_uri", env.getProperty("google.redirect.uri"))
//                    .add("code", code)
//                    .add("grant_type", "authorization_code")
//                    .build())
//            .execute()
//            .returnContent()
//            .asString();
//    ObjectMapper mapper = new ObjectMapper();
//    JsonNode node = mapper.readTree(response).get("access_token");
//
//    System.out.println(node.textValue());
//    return node.textValue();
//  }

//  public GooglePojo getUserInfoV1(final String accessToken)
//      throws ClientProtocolException, IOException {
//    String link = env.getProperty("google.link.get.user_info_v1") + accessToken;
//    String response = Request.Get(link).execute().returnContent().asString();
//    ObjectMapper mapper = new ObjectMapper();
//    GooglePojo googlePojo = mapper.readValue(response, GooglePojo.class);
//    System.out.println(googlePojo);
//    return googlePojo;
//  }

//  public GooglePojo getUserInfoV3(final String accessToken)
//          throws ClientProtocolException, IOException {
//    String link = env.getProperty("google.link.get.user_info_v3") + accessToken;
//    String response = Request.Get(link).execute().returnContent().asString();
//    ObjectMapper mapper = new ObjectMapper();
//    GooglePojo googlePojo = mapper.readValue(response, GooglePojo.class);
//    System.out.println(googlePojo);
//    return googlePojo;
//  }

  // credential = accessToken = token
  public GooglePojo getUserInfoFromCredential(final String credential)
          throws ClientProtocolException, IOException, GeneralSecurityException, NoSuchFieldException, IllegalAccessException {

    GoogleIdTokenVerifier verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList(env.getProperty("google.app.id")))
            // Or, if multiple clients access the backend:
            // .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build();

    // (Receive idTokenString by HTTPS POST)

    GoogleIdToken idToken = verifier.verify(credential);
    System.out.println("Id token: = " + idToken);
    GooglePojo googlePojo = null;
    if (idToken != null) {
      Payload payload = idToken.getPayload();

      // Print user identifier
      String userId = payload.getSubject();
      System.out.println("User ID: " + userId);

      // Get profile information from payload
      String email = payload.getEmail();
      boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());

      // These seven fields are only included when the user has granted the "profile" and
      // "email" OAuth scopes to the application.
      /*      "email": "testuser@gmail.com",
      "email_verified": "true",
      "name" : "Test User",
      "picture": "https://lh4.googleusercontent.com/-kYgzyAWpZzJ/ABCDEFGHI/AAAJKLMNOP/tIXL9Ir44LE/s99-c/photo.jpg",
      "given_name": "Test",
      "family_name": "User",
      "locale": "en"*/

      String name = (String) payload.get("name");
      String pictureUrl = (String) payload.get("picture");
      String locale = (String) payload.get("locale");
      String familyName = (String) payload.get("family_name");
      String givenName = (String) payload.get("given_name");

      googlePojo = new GooglePojo();
      googlePojo.setEmail(email);
      googlePojo.setVerified_email(emailVerified);
      googlePojo.setLocale(locale);
      googlePojo.setFamily_name(familyName);
      googlePojo.setGiven_name(givenName);
      googlePojo.setPicture(pictureUrl);
      googlePojo.setName(name);

      System.out.println(googlePojo);

    } else {
      System.out.println("Invalid ID token.");
    }
    if (googlePojo == null) {
      throw new CustomException("Invalid ID token when get user info from google token");
    }
    return googlePojo;
  }

  public UserDetails buildUser(GooglePojo googlePojo) {
    boolean enabled = true;
    boolean accountNonExpired = true;
    boolean credentialsNonExpired = true;
    boolean accountNonLocked = true;
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    UserDetails userDetail =
        new User(
            googlePojo.getEmail(),
            "",
            enabled,
            accountNonExpired,
            credentialsNonExpired,
            accountNonLocked,
            authorities);
    return userDetail;
  }
}
