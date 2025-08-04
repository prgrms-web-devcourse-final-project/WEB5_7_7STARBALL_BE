package sevenstar.marineleisure.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

/**
 * 레이트 리밋 설정
 * Bucket4j와 Redis를 사용하여 분산 환경에서 API 요청 속도 제한을 구현합니다.
 */
@Configuration
public class RateLimitConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.data.redis.password:}")
	private String redisPassword;

	@Value("${spring.redis.ssl:false}")
	private boolean redisSsl;

	/**
	 * Redis 클라이언트 빈 등록
	 */
	@Bean
	public RedisClient redisClient() {
		RedisURI.Builder builder = RedisURI.builder()
			.withHost(redisHost)
			.withPort(redisPort);

		if (redisPassword != null && !redisPassword.isBlank()) {
			builder.withPassword(redisPassword.toCharArray());
		}

		if (redisSsl) {
			builder.withSsl(true);
		}

		return RedisClient.create(builder.build());
	}

	@Bean
	public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
		RedisCodec<String, byte[]> codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
		return redisClient.connect(codec);
	}

	@Bean
	public ProxyManager<String> bucketProxyManager(StatefulRedisConnection<String, byte[]> connection) {

		return Bucket4jLettuce
			.casBasedBuilder(connection.async()) // RedisAsyncCommands<String, byte[]>
			.expirationAfterWrite( // 만료 전략 적용
				ExpirationAfterWriteStrategy
					.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1))
			)
			.build();
	}

	@Bean
	public Bandwidth globalBandwidth() {
		return Bandwidth.builder()
			.capacity(100)
			.refillIntervally(100, Duration.ofSeconds(1))
			.build();
	}

	// IP 기반 레이트 리밋 (IP 초당 10개)
	@Bean
	public Bandwidth ipBandwidth() {
		return Bandwidth.builder()
			.capacity(50)
			.refillIntervally(50, Duration.ofSeconds(1))
			.build();
	}

	// 인증 엔드포인트 전용 레이트 리밋 (IP당 1초에 5개)
	@Bean
	public Bandwidth authBandwidth() {
		return Bandwidth.builder()
			.capacity(5)
			.refillIntervally(5, Duration.ofSeconds(1))
			.build();
	}
}
