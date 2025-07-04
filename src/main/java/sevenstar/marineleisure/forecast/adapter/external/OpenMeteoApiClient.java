package sevenstar.marineleisure.forecast.adapter.external;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OpenMeteoApiClient {

	private final RestTemplate restTemplate = new RestTemplate();

	public String callApi(double latitude, double longitude) {
		String url = UriComponentsBuilder.fromHttpUrl("https://api.open-meteo.com/v1/forecast")
			.queryParam("latitude", latitude)
			.queryParam("longitude", longitude)
			.queryParam("daily", "sunrise,sunset,uv_index_max")
			.queryParam("timezone", "Asia/Seoul")
			.build()
			.toUriString();

		return restTemplate.getForObject(url, String.class);
	}
}
