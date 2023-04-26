package hcmute.puzzle.configuration;

import hcmute.puzzle.interceptor.CommonInterceptor;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class WebMcvConfig implements WebMvcConfigurer {

  @Autowired CommonInterceptor commonInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // this interceptor will be applied to all URLs
    registry.addInterceptor(commonInterceptor);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedMethods("*")
        .allowedOrigins(Constant.LOCAL_URL, Constant.ONLINE_URL);
  }

//  @Bean
//  public WebMvcConfigurer corsConfigurer() {
//    return new WebMvcConfigurer() {
//      @Override
//      public void addCorsMappings(CorsRegistry registry) {
//        registry
//            .addMapping("/**")
//            .allowedMethods("*")
//            .allowedOrigins(Constant.LOCAL_URL, Constant.ONLINE_URL);
//      }
//    };
//  }
}
