package hcmute.puzzle.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.util.*;

@Slf4j
@Configuration
public class BeanConfiguration {

	//    @Autowired
	//    DataSource dataSource;

	@Value("${spring.liquibase.enabled}")
	boolean shouldRun;


	@Value("${spring.liquibase.change-log}")
	String changeLogPath;

	@Autowired
	Environment environment;

	//https://www.devglan.com/online-tools/jasypt-online-encryption-decryption
	@Bean(name = "jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(environment.getProperty("jasypt.encryptor.password"));
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);
		return encryptor;
	}

	@Bean
	public SpringLiquibase liquibase(DataSource dataSource) {
		log.info("--------Configuring Liquibase----------");
		Map<String, String> parameters = new HashMap<>();
		parameters.put("overwrite-output-file", "true");
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setChangeLog(changeLogPath);
		liquibase.setDataSource(dataSource);
		liquibase.setShouldRun(shouldRun);
		liquibase.setChangeLogParameters(parameters);
		return liquibase;
	}

	@Bean
	public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
			ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
			EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
			WebEndpointProperties webEndpointProperties, Environment environment) {
		List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
		Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
		allEndpoints.addAll(webEndpoints);
		allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
		allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
		String basePath = webEndpointProperties.getBasePath();
		EndpointMapping endpointMapping = new EndpointMapping(basePath);
		boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment,
																			 basePath);
		return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
												corsProperties.toCorsConfiguration(),
												new EndpointLinksResolver(allEndpoints, basePath),
												shouldRegisterLinksMapping);
	}

	private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment,
			String basePath) {
		return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(
				basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
	}

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
				new ClassPathResource("firebase-service-account.json").getInputStream());
		FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googleCredentials).build();
		FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "puzzle-ute");
		return FirebaseMessaging.getInstance(app);
	}

	    @Bean(name = "multipartResolver")
	    public StandardServletMultipartResolver standardServletMultipartResolver() {
	        return new StandardServletMultipartResolver ();
	    }

//	@Bean
////	@Order(0)
//	public MultipartFilter multipartFilter() {
//		MultipartFilter multipartFilter = new MultipartFilter();
//		multipartFilter.setMultipartResolverBeanName("multipartResolver");
//		return multipartFilter;
//	}


	//    @Bean
	//    public UserDetailsService users() {
	//        // The builder will ensure the passwords are encoded before saving in memory
	//        User.UserBuilder users = User.withDefaultPasswordEncoder();
	//        UserDetails user = users
	//                .username("user")
	//                .password("password")
	//                .roles("USER")
	//                .build();
	//        UserDetails admin = users
	//                .username("admin")
	//                .password("password")
	//                .roles("USER", "ADMIN")
	//                .build();
	//        return new InMemoryUserDetailsManager(user, admin);
	//    }

}
