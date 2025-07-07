package sevenstar.marineleisure.global.api.config.properties;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.Getter;
import sevenstar.marineleisure.global.enums.ActivityCategory;

@Getter
@ConfigurationProperties(prefix = "api.khoa")
public class KhoaProperties {

	private final String baseUrl;
	private final String serviceKey;
	private final String type;
	private final Path path;

	public KhoaProperties(String baseUrl, String serviceKey, String type, Path path) {
		this.baseUrl = baseUrl;
		this.serviceKey = serviceKey;
		this.type = type;
		this.path = path;
	}

	@Getter
	public static class Path {
		private final String fishing;
		private final String mudflat;
		private final String diving;
		private final String surfing;

		public Path(String fishing, String mudflat, String diving, String surfing) {
			this.fishing = fishing;
			this.mudflat = mudflat;
			this.diving = diving;
			this.surfing = surfing;
		}
	}

	public String getPath(ActivityCategory category) {
		return switch (category) {
			case FISHING -> path.getFishing();
			case MUDFLAT -> path.getMudflat();
			case DIVING -> path.getDiving();
			case SURFING -> path.getSurfing();
		};
	}

	public MultiValueMap<String, String> getParams(String reqDate, int page, int size) {
		return getDefaultParams(reqDate, page, size);
	}

	public MultiValueMap<String, String> getParams(String reqDate, int page, int size, String gubun) {
		MultiValueMap<String, String> defaultParams = getDefaultParams(reqDate, page, size);
		defaultParams.add("gubun", URLEncoder.encode(gubun, StandardCharsets.UTF_8));
		return defaultParams;
	}

	private MultiValueMap<String, String> getDefaultParams(String reqDate, int page, int size) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("serviceKey", URLEncoder.encode(serviceKey, StandardCharsets.UTF_8));
		params.add("type", type);
		params.add("reqDate", reqDate);
		params.add("pageNo", String.valueOf(page));
		params.add("numOfRows", String.valueOf(size));
		return params;
	}
}
