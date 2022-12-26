package hcmute.puzzle.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import hcmute.puzzle.accessHandler.CustomAccessDeniedHandler;
import hcmute.puzzle.accessHandler.CustomAuthenticationHandler;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.services.CustomOAuth2UserService;
import hcmute.puzzle.utils.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.io.IOException;

// @EnableJpaRepositories(basePackages="java")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
  private static final String[] AUTH_WHITE_LIST = {
    "/v3/api-docs/**", "/swagger-ui/**", "/v2/api-docs/**", "/swagger-resources/**"
  };

  //    @Autowired
  //    private AuthEntryPointJwt unauthorizedHandler;

  @Autowired CustomOAuth2UserService oauthUserService;

  @Autowired OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  @Autowired UserRepository userRepository;

  @Autowired UserService userService;

  //  @Autowired
  //  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
  //    auth.inMemoryAuthentication()
  //        .withUser("guest")
  //        .password(passwordEncoder().encode("guest_pass"))
  //        .authorities("GUEST");
  //  }

  @Bean
  FirebaseMessaging firebaseMessaging() throws IOException {
    GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
    FirebaseOptions firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build();
    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "puzzle-ute");
    return FirebaseMessaging.getInstance(app);
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  // https://www.codejava.net/frameworks/spring-boot/fix-websecurityconfigureradapter-deprecated
  // https://stackoverflow.com/questions/72381114/spring-security-upgrading-the-deprecated-websecurityconfigureradapter-in-spring
  //    @Bean(BeanIds.AUTHENTICATION_MANAGER)
  //    public AuthenticationManager authenticationManagerBean(AuthenticationManagerBuilder builder)
  // throws Exception {
  //        // Get AuthenticationManager bean
  //        return
  // builder.userDetailsService(userService).passwordEncoder(passwordEncoder()).and().build();
  //    }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new CustomAuthenticationHandler();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // Password encoder, để Spring Security sử dụng mã hóa mật khẩu người dùng
    return new BCryptPasswordEncoder();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors()
        .disable()
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint())
        .accessDeniedHandler(accessDeniedHandler())
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/api/common/**")
        .permitAll()
        .antMatchers("/api/test/**")
        .permitAll()
        .antMatchers(AUTH_WHITE_LIST)
        .permitAll()
        .antMatchers("/api/user/**")
        .hasAuthority(Roles.USER.value)
        .antMatchers("/api/login/**", "/api/login-google/**", "/api/forgot-password", "/api/reset-password")
        .permitAll()
        .antMatchers("/api/role/admin")
        .hasAnyAuthority(Roles.ADMIN.value)
        .antMatchers("/api/candidate/**")
        .hasAuthority(Roles.CANDIDATE.value)
        .antMatchers("/api/employer/**")
        .hasAuthority(Roles.EMPLOYER.value)
        .antMatchers("/api/admin/**")
        .hasAuthority(Roles.ADMIN.value)
        .antMatchers("/api/init-db")
        .permitAll()
        .antMatchers("/oauth2/**")
        .permitAll()
        .antMatchers("/api/pay/**")
        .hasAuthority(Roles.EMPLOYER.value)
        .antMatchers("/api/pay-result/**")
        .permitAll()
        //        .antMatchers("/api/auth/**")
        //        .permitAll()
        //        .antMatchers("/api/test/**")
        //        .permitAll()
        //        .antMatchers("/api/v1/**")
        //        .permitAll()
        .antMatchers("/swagger-ui/**", "/puzzle-api/**")
        .permitAll()
        .antMatchers("/", "/login", "/login-google", "/oauth/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .permitAll()
        .and()
        .rememberMe()
        .key("AbcdEfghIjklmNopQrsTuvXyz_0123456789")
        .and()
        .logout()
        .permitAll()
        .and()
        .oauth2Login()
        .loginPage("/login")
        .userInfoEndpoint()
        .userService(oauthUserService)
        .and()
        .successHandler(oAuth2LoginSuccessHandler)
        .and()
        .httpBasic();

    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  //    @Bean
  //    public WebSecurityCustomizer webSecurityCustomizer() {
  //      return (web) ->
  //          web.ignoring()
  //              .antMatchers(
  //                  "/images/**", "/js/**", "/webjars/**", "/css/**", "/lib/**", "/favicon.ico");
  //    }
  // https://viblo.asia/p/securing-spring-boot-with-jwt-part-2-xac-thuc-nguoi-dung-dua-tren-du-lieu-trong-co-so-du-lieu-63vKjnJVK2R
}
