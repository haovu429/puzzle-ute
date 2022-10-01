package hcmute.puzzle.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

  @Value("${redis.host}")
  private String redisHost;

  @Value("${redis.port}")
  private Integer redisPort;

  @Value("${redis.pass}")
  private String redisPass;

  @Bean
  @Primary
  JedisConnectionFactory jedisConnectionFactory() throws Exception {
    RedisStandaloneConfiguration redisStandaloneConfiguration =
        new RedisStandaloneConfiguration(redisHost, redisPort);
    if (redisPass != null) {
      redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPass));
    }
    return new JedisConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean
  RedisTemplate<String, Object> redisTemplate() throws Exception {
    final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
    template.setConnectionFactory(jedisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());

    template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    return template;
  }
}
