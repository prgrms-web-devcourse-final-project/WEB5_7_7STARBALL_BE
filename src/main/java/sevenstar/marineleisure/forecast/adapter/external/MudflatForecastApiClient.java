package sevenstar.marineleisure.forecast.adapter.external;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 갯벌지수를 받기위한 외부  API연결
 */
@Component
public class MudflatForecastApiClient {
	@Value("${dataportal.api.key}")
	private String serviceKey;

	private final RestTemplate restTemplate = new RestTemplate();
	String baseUrl = "https://apis.data.go.kr/1192136/fcstMudflat/GetFcstMudflatApiService";

	public String callApi(){
		URI uri = UriComponentsBuilder.fromUriString(baseUrl)
			.queryParam("serviceKey", URLEncoder.encode(serviceKey, StandardCharsets.UTF_8))
			.queryParam("type", "json")
			.queryParam("reqDate", "2025070200")
			.queryParam("pageNo", 1)
			.queryParam("numOfRows", 3)
			.build(true)
			.toUri();
		System.out.println(uri);
		ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

		return response.getBody();
	}
}
