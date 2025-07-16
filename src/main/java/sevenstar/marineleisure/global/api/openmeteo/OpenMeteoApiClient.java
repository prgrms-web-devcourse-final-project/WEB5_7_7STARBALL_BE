package sevenstar.marineleisure.global.api.openmeteo;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.config.properties.OpenMeteoProperties;
import sevenstar.marineleisure.global.api.openmeteo.dto.common.OpenMeteoReadResponse;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.SunTimeItem;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.UvIndexItem;
import sevenstar.marineleisure.global.utils.UriBuilder;

@Component
@RequiredArgsConstructor
public class OpenMeteoApiClient {
	private final RestTemplate restTemplate;
	private final OpenMeteoProperties openMeteoProperties;

	public ResponseEntity<OpenMeteoReadResponse<SunTimeItem>> getSunTimes(
		ParameterizedTypeReference<OpenMeteoReadResponse<SunTimeItem>> responseType,
		LocalDate startDate, LocalDate endDate, double latitude, double longitude) {
		URI uri = UriBuilder.buildQueryParameter(openMeteoProperties.getBaseUrl(),
			openMeteoProperties.getSunriseSunsetParams(startDate, endDate, latitude, longitude));

		return restTemplate.exchange(uri, HttpMethod.GET, null, responseType);
	}

	public ResponseEntity<OpenMeteoReadResponse<UvIndexItem>> getUvIndex(
		ParameterizedTypeReference<OpenMeteoReadResponse<UvIndexItem>> responseType,
		LocalDate startDate, LocalDate endDate, double latitude, double longitude) {
		URI uri = UriBuilder.buildQueryParameter(openMeteoProperties.getBaseUrl(),
			openMeteoProperties.getUvIndexParams(startDate, endDate, latitude, longitude));

		return restTemplate.exchange(uri, HttpMethod.GET, null, responseType);
	}
}
