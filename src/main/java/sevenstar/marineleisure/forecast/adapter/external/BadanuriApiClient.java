package sevenstar.marineleisure.forecast.adapter.external;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class BadanuriApiClient {
	private final RestTemplate restTemplate = new RestTemplate();
	String baseUrl = "http://www.khoa.go.kr/api/oceangrid/tideObsPreTab/search.do";
	@Value("${badanuri.api.key}")
	private String serviceKey;

	public String callApi() {
		URI uri = UriComponentsBuilder.fromUriString(baseUrl)
			.queryParam("ServiceKey", serviceKey)
			.queryParam("Date", "20250703")
			.queryParam("ObsCode", "DT_0001")
			.queryParam("ResultType", "json")
			.build()
			.encode()
			.toUri();
		System.out.println(uri);
		ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

		return response.getBody();
	}
}
