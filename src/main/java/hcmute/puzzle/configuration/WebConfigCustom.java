package hcmute.puzzle.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfigCustom {
  //    @Configuration
  //    public class WebConfiguration implements WebMvcConfigurer {
  //
  //        @Override
  //        public void addCorsMappings(CorsRegistry registry) {
  //            registry.addMapping("/**");
  //        }
  //    }
//  @Bean
//  public WebMvcConfigurer corsConfigurer() {
//    return new WebMvcConfigurerAdapter() {
//      @Override
//      public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
//      }
//    };
//  }
}
