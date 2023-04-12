package hcmute.puzzle;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import hcmute.puzzle.utils.Constant;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
@EnableJpaAuditing
// @EnableAutoConfiguration
// http://localhost:8080/swagger-ui/index.html
// http://localhost:8080/oauth2/authorization/google
public class PuzzleUteApplication {

  public static void main(String[] args) {
    System.setProperty("user.timezone", "Asia/Ho_Chi_Minh");
    SpringApplication.run(PuzzleUteApplication.class, args);
  }
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(Constant.LOCAL_URL, Constant.ONLINE_URL);
      }
    };
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
