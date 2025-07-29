package sevenstar.marineleisure.global.api.kakao;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

	@CircuitBreaker(name = "kakao-api", fallbackMethod = "fallbackKakaoApi")
	public ResponseEntity<RegionResponse> get(float latitude, float longitude) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("y", String.valueOf(latitude));
		params.add("x", String.valueOf(longitude));

		URI uri = UriBuilder.buildQueryParameter(kakaoMapUri, params);

		return kakaoRestTemplate.exchange(uri, HttpMethod.GET, null, RegionResponse.class);
	}

	public ResponseEntity<RegionResponse> fallbackKakaoApi(float latitude, float longitude, Throwable t) {
		log.error("Kakao API 호출에 실패하여 fallback 메서드가 실행되었습니다. message: {}", t.getMessage());
		RegionResponse fallbackResponse = new RegionResponse();
		RegionResponse.Document fallbackDocument = new RegionResponse.Document();
		fallbackDocument.setAddress_name("알 수 없는 지역");
		fallbackResponse.setDocuments(List.of(fallbackDocument));
		return ResponseEntity.ok(fallbackResponse);
	}

}
