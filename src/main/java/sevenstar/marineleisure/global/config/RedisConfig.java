package sevenstar.marineleisure.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;



/**
 * Redis 설정
 * 토큰 블랙리스트 관리를 위한 Redis 설정을 제공합니다.
 */
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.ssl:false}")
    private boolean redisSsl;

    /**
     * RedisConnectionFactory 빈 등록
     * application.yml의 spring.redis 설정을 바탕으로 StandaloneConfiguration 및 SSL 옵션을 구성합니다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Standalone 설정
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        if (redisPassword != null && !redisPassword.isBlank()) {
            config.setPassword(RedisPassword.of(redisPassword));
        }

        // Lettuce 클라이언트 설정
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
        if (redisSsl) {
            builder.useSsl().disablePeerVerification();
        }
        LettuceClientConfiguration clientConfig = builder.build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    /**
     * RedisTemplate 빈 등록
     * 키는 String, 값은 JSON으로 직렬화하여 저장합니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }
}

/**
 * Redis 설정
 * 토큰 블랙리스트 관리를 위한 Redis 설정을 제공합니다.
 */
//@Configuration
//public class RedisConfig {
//
//    /**
//     * Redis 연결 팩토리
//     * 기본 설정으로 localhost:6379에 연결합니다.
//     */
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory();
//    }
//
//    /**
//     * Redis 템플릿
//     * 키는 문자열, 값은 JSON으로 직렬화하여 저장합니다.
//     */
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory());
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
//        return template;
//    }
//}