package sevenstar.marineleisure.global.utils;

import java.net.URI;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UriBuilder {
	public URI buildQueryParameter(String baseUrl, String path, MultiValueMap<String, String> params) {
		return UriComponentsBuilder.fromHttpUrl(baseUrl).path(path).queryParams(params).build(true).toUri();
	}
}
