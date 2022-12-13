package hcmute.puzzle.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static hcmute.puzzle.utils.Constant.LIST_HOST_FRONT_END;

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
          .allowedOrigins(LIST_HOST_FRONT_END)
          .allowCredentials(true)
          .maxAge(3600);
      ;
    }
  }
}
