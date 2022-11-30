package hcmute.puzzle;

import hcmute.puzzle.utils.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebMvc
//@EnableSwagger2
//@EnableAsync
// @OpenAPIDefinition(info = @Info(title = "Employees API", version = "2.0", description =
// "Employees Information"))
// @SecurityScheme(name = "javainuseapi", scheme = "basic", type = SecuritySchemeType.HTTP, in =
// SecuritySchemeIn.HEADER)
// @EnableAutoConfiguration
// http://localhost:8080/swagger-ui/index.html
// http://localhost:8080/oauth2/authorization/google
public class PuzzleUteApplication {

  public static void main(String[] args) {
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
}
