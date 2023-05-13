package hcmute.puzzle;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

// http://localhost:8080/api/swagger-ui/index.html?configUrl=api/api-docs -> search for "/api/api-docs"
// http://localhost:8080/oauth2/authorization/google
//@OpenAPIDefinition(info = @Info(title = "Puzzle API", version = "v1"))
//@SecurityScheme(name = "puzzle", bearerFormat = "JWT", scheme = "bearer",
//        type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@EnableEncryptableProperties
@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@EnableWebMvc
@EnableAsync(proxyTargetClass=true)
// @EnableAutoConfiguration
public class PuzzleUteApplication {

  @PostConstruct
  public void init(){
    // Setting Spring Boot SetTimeZone
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
  }
  public static void main(String[] args) {
    System.setProperty("user.timezone", "Asia/Ho_Chi_Minh");
    SpringApplication.run(PuzzleUteApplication.class, args);
  }





//  @Bean
//  FirebaseMessaging firebaseMessaging() throws IOException {
//    GoogleCredentials googleCredentials = GoogleCredentials
//            .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
//    FirebaseOptions firebaseOptions = FirebaseOptions
//            .builder()
//            .setCredentials(googleCredentials)
//            .build();
//    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
//    return FirebaseMessaging.getInstance(app);
//  }

//  @Bean
//  CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOrigins(Arrays.asList("https://example.com"));
//    configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }
}
