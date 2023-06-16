//package hcmute.puzzle.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
//import java.util.TimeZone;
//
//@Slf4j
//@Configuration
////@AutoConfigureBefore({JacksonAutoConfiguration.class})
//public class ConvertTimeToJsonConfig {
//	/**
//	 * Warning: If in project have use @EnableWebMvc to config,
//	 * then time format json is not working, you must remove it and use class implement WebMvcConfigurer
//	 */
//
//	private static final String dateFormat = "yyyy-MM-dd";
//	private static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";
//
//	@Bean
//	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//		log.warn("====== Config time to json=======");
//		return builder -> {
//			builder.simpleDateFormat(dateTimeFormat);
//			builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
//			builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
//			builder.timeZone(TimeZone.getDefault());
//		};
//	}
//
//	//    @Bean
//	//    public ObjectMapper objectMapper() {
//	//        ObjectMapper objectMapper = new ObjectMapper();
//	//        // Customize the object mapper here
//	//        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss"));
//	//        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
//	//        return objectMapper;
//	//    }
//
//}