package hcmute.puzzle.configuration.security;

import hcmute.puzzle.interceptor.CommonInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import static hcmute.puzzle.utils.Constant.LIST_HOST_FRONT_END;

@Configuration
//@EnableWebMvc
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
            .allowedOrigins(LIST_HOST_FRONT_END)
            .allowCredentials(true)
            .maxAge(3600);
  }
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/conf/**")
            .addResourceLocations("classpath:/conf/");
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
