package sevenstar.marineleisure.global.api.kakao;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.api.kakao.dto.RegionResponse;
import sevenstar.marineleisure.global.utils.UriBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoApiClient {

	@Value("${kakao.map.uri}")
	private String kakaoMapUri;

	private final RestTemplate kakaoRestTemplate;

	// RateLimiter 설정 (카카오 역지오코딩 API에 최적화)
	private final RateLimiter rateLimiter = RateLimiter.of(
		"kakao-api",
		RateLimiterConfig.custom()
			.limitForPeriod(25) // 초당 25회 → 30회 제한보다 안전하게
			.limitRefreshPeriod(Duration.ofSeconds(1))
			.timeoutDuration(Duration.ofSeconds(2)) // burst 대응 위해 대기 2초
			.build()
	);

	// Retry 설정 (429, 5xx, 네트워크 오류에만 재시도)
	private final Retry retry = Retry.of(
		"kakao-api",
		RetryConfig.custom()
			.maxAttempts(3) // 최대 3회 시도
			.waitDuration(Duration.ofMillis(500)) // 0.5초 간격으로 재시도
			.retryExceptions(
				HttpServerErrorException.class,
				ResourceAccessException.class
			)
			.retryOnException(ex -> {
				if (ex instanceof HttpClientErrorException.TooManyRequests) { // 429
					log.warn("429 Too Many Requests - 재시도");
					return true;
				}
				return false;
			})
			.ignoreExceptions(HttpClientErrorException.BadRequest.class) // 잘못된 요청은 재시도 안 함
			.build()
	);

	public ResponseEntity<RegionResponse> get(float latitude, float longitude) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("y", String.valueOf(latitude));
		params.add("x", String.valueOf(longitude));

		URI uri = UriBuilder.buildQueryParameter(kakaoMapUri, params);

		Supplier<ResponseEntity<RegionResponse>> supplier =
			() -> kakaoRestTemplate.exchange(uri, HttpMethod.GET, null, RegionResponse.class);

		try {
			return Decorators.ofSupplier(supplier)
				.withRateLimiter(rateLimiter)
				.withRetry(retry)
				.decorate()
				.get();
		} catch (Exception e) {
			log.error("Kakao API 호출 중 예외 발생: {}", e.getMessage());
			return fallbackKakaoApi(latitude, longitude, e);
		}
	}

	public ResponseEntity<RegionResponse> fallbackKakaoApi(float latitude, float longitude, Throwable t) {
		log.error("Kakao API 호출 실패, fallback 실행: {}", t.getMessage());
		RegionResponse fallbackResponse = new RegionResponse();
		RegionResponse.Document fallbackDocument = new RegionResponse.Document();
		fallbackDocument.setAddress_name("알 수 없는 지역");
		fallbackResponse.setDocuments(List.of(fallbackDocument));
		return ResponseEntity.ok(fallbackResponse);
	}
}
