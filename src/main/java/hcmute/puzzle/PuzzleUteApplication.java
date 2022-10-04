package hcmute.puzzle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
// @OpenAPIDefinition(info = @Info(title = "Employees API", version = "2.0", description =
// "Employees Information"))
// @SecurityScheme(name = "javainuseapi", scheme = "basic", type = SecuritySchemeType.HTTP, in =
// SecuritySchemeIn.HEADER)
// @EnableAutoConfiguration
// http://localhost:8080/swagger-ui/index.html
public class PuzzleUteApplication {

  public static void main(String[] args) {
    SpringApplication.run(PuzzleUteApplication.class, args);
  }
}
