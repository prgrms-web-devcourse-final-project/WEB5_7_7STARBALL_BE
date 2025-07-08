package sevenstar.marineleisure.global.utils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UriBuilder {
	public String encodeString(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	public URI buildQueryParameter(String baseUrl, String path, MultiValueMap<String, String> params) {
		return UriComponentsBuilder.fromHttpUrl(baseUrl).path(path).queryParams(params).build(true).toUri();
	}
}
