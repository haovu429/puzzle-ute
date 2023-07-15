package hcmute.puzzle.configuration.security;

import hcmute.puzzle.configuration.accessHandler.CustomAccessDeniedHandler;
import hcmute.puzzle.configuration.accessHandler.CustomAuthenticationHandler;
import hcmute.puzzle.filter.JwtAuthenticationFilter;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.paypal.PaymentController;
import hcmute.puzzle.services.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

import static hcmute.puzzle.utils.Constant.AuthPath.VERIFY_ACCOUNT_URL;
import static org.springframework.security.config.Customizer.withDefaults;

// @EnableJpaRepositories(basePackages="java")
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
	private static final String[] AUTH_WHITE_LIST_DOC_RESOURCE = {"/v3-docs/**", "/swagger-ui/**", "/v2-docs/**",
			"/swagger-resources/**", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html",
			"/v3/api-docs/**", "/doc", "/static/images/**"};

	private static final String[] AUTH_WHITE_LIST_BUSINESS = {"/common/**", "/schedule-config/**", "/test/**",
			"/init-db", "/oauth2/**", "/api-docs", "/actuator/**", "/login/**", "/auth/**", "/login-google/**",
			"/forgot-password", "/reset-password", "/", "/login-google", "/oauth/**", "/pay-result/**", "/system/**",
			"/lab/**", "/redirect/**"};

	//    @Autowired
	//    private AuthEntryPointJwt unauthorizedHandler;

//	@Autowired
//	CustomOAuth2UserService oauthUserService;

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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
			Exception {
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

		http.cors((cors) -> cors.disable())
			.csrf((csrf) -> csrf.disable())
			.exceptionHandling(
					(exceptionHandling) -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint())
															.accessDeniedHandler(accessDeniedHandler()))
			.sessionManagement((sessionManagement) -> sessionManagement.sessionConcurrency(
																			   (sessionConcurrency) -> sessionConcurrency.maximumSessions(1).expiredUrl("/login?expired"))
																	   .sessionCreationPolicy(
																			   SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(
					(authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(AUTH_WHITE_LIST_BUSINESS)
																	.permitAll()
																	.requestMatchers(AUTH_WHITE_LIST_DOC_RESOURCE)
																	.permitAll()
																	.requestMatchers("/user/**", "/payment/**")
																	.hasAnyAuthority(Roles.USER.getValue(),
																					 Roles.ADMIN.getValue())
																	.requestMatchers("/role/admin", "/admin/**")
																	.hasAnyAuthority(Roles.ADMIN.getValue())
																	.requestMatchers("/candidate/**")
																	.hasAnyAuthority(Roles.CANDIDATE.getValue())
																	.requestMatchers("/employer/**")
																	.hasAnyAuthority(Roles.EMPLOYER.getValue())
																	.anyRequest()
																	.authenticated())
			.httpBasic(withDefaults())
			.rememberMe((rememberMe) -> rememberMe.key(java.util.UUID.randomUUID().toString())
												  .tokenValiditySeconds(1209600));
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
