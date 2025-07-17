package sevenstar.marineleisure.global.api.kakao;

import java.net.URI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.kakao.dto.RegionResponse;
import sevenstar.marineleisure.global.utils.UriBuilder;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {
	@Value("${kakao.map.uri}")
	private String kakaoMapUri;

	private final RestTemplate kakaoRestTemplate;

	public ResponseEntity<RegionResponse> get(float latitude, float longitude) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("y", String.valueOf(latitude));
		params.add("x", String.valueOf(longitude));

		URI uri = UriBuilder.buildQueryParameter(kakaoMapUri, params);

		return kakaoRestTemplate.exchange(uri, HttpMethod.GET, null, RegionResponse.class);
	}

}
