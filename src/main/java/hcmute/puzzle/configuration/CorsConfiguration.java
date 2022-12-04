package hcmute.puzzle.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
  //    @Bean
  //    public WebMvcConfigurer corsConfigurer() {
  //        return new WebMvcConfigurerAdapter() {
  //
  //        };
  //    }

  //    @Override
  //    public void addCorsMappings(CorsRegistry registry) {
  //        registry.addMapping("/**");
  ////                .allowedMethods("*")
  ////                .allowedOrigins(Constant.LOCAL_URL, Constant.ONLINE_URL);
  //    }
  @Configuration
  public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry
          .addMapping("/**")
          .allowedMethods("*")
          .allowedOrigins("http://localhost:8000", "http://localhost:8080", "http://localhost:5000")
          .allowCredentials(true)
          .maxAge(3600);
      ;
    }
  }
}
