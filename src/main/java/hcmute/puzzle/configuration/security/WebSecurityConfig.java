package hcmute.puzzle.configuration.security;

import hcmute.puzzle.configuration.accessHandler.CustomAccessDeniedHandler;
import hcmute.puzzle.configuration.accessHandler.CustomAuthenticationHandler;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.services.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

// @EnableJpaRepositories(basePackages="java")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
    private static final String[] AUTH_WHITE_LIST = {"/v3-docs/**", "/swagger-ui/**", "/v2-docs/**",
            "/swagger-resources/**", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html",
            "/v3/api-docs/**"};

    //    @Autowired
    //    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    CustomOAuth2UserService oauthUserService;

    @Autowired
    UserDetailsService userDetailsService;

    //  @Autowired OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    UserRepository userRepository;

    @Autowired
	UserSecurityService userService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

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
            .antMatchers("/common/**", "/schedule-config/**", "/test/**", "/init-db"
                    , "/oauth2/**", "/api-docs", "/actuator/**", "/login/**", "/auth/**"
                    , "/login-google/**", "/forgot-password", "/reset-password"
                    , "/", "/login-google", "/oauth/**", "/pay-result/**")
            .permitAll()
            .antMatchers("/static/images/**")
            .permitAll()
            .antMatchers(AUTH_WHITE_LIST)
            .permitAll()
            .antMatchers("/user/**")
            .hasAuthority(Roles.USER.value)
            .antMatchers("/role/admin", "/admin/**")
            .hasAnyAuthority(Roles.ADMIN.value)
            .antMatchers("/candidate/**")
            .hasAuthority(Roles.CANDIDATE.value)
            .antMatchers("/employer/**", "/pay/**")
            .hasAuthority(Roles.EMPLOYER.value)
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .rememberMe()
            .key(java.util.UUID.randomUUID().toString())
            .tokenValiditySeconds(1209600);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//      @Bean
//      public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) ->
//            web.ignoring()
//                .antMatchers(
//                    "/images/**", "/js/**", "/webjars/**", "/css/**", "/lib/**", "/favicon.ico");
//      }
    //https://viblo.asia/p/securing-spring-boot-with-jwt-part-2-xac-thuc-nguoi-dung-dua-tren-du-lieu-trong-co-so-du-lieu-63vKjnJVK2R
}
