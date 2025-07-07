package sevenstar.marineleisure.global.api.khoa;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.config.properties.KhoaProperties;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.utils.UriBuilder;

@Component
@RequiredArgsConstructor
public class KhoaApiClient {
	private final RestTemplate restTemplate;
	private final KhoaProperties khoaProperties;

	public <T> ResponseEntity<T> get(Class<T> responseType, String reqDate, int page, int size,
		ActivityCategory category) {
		if (category == ActivityCategory.FISHING) {
			// throw new IllegalAccessException();
		}
		URI uri = UriBuilder.buildQueryParameter(khoaProperties.getBaseUrl(), khoaProperties.getPath(category),
			khoaProperties.getParams(reqDate, page, size));
		return restTemplate.getForEntity(uri, responseType);
	}

	public <T> ResponseEntity<T> get(Class<T> responseType, String reqDate, int page, int size, String gubun) {
		URI uri = UriBuilder.buildQueryParameter(khoaProperties.getBaseUrl(),
			khoaProperties.getPath(ActivityCategory.FISHING), khoaProperties.getParams(reqDate, page, size, gubun));
		return restTemplate.getForEntity(uri, responseType);
	}

}
