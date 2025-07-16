package sevenstar.marineleisure.global.api.config.properties;

import java.time.LocalDate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "api.openmeteo")
public class OpenMeteoProperties {
	private final String baseUrl;
	private final String timezone;

	public OpenMeteoProperties(String baseUrl, String timezone) {
		this.baseUrl = baseUrl;
		this.timezone = timezone;
	}

	public MultiValueMap<String, String> getSunriseSunsetParams(LocalDate startDate, LocalDate endDate, double latitude,
		double longitude) {
		return getDefaultParams("sunrise,sunset", startDate, endDate, latitude, longitude);
	}

	public MultiValueMap<String, String> getUvIndexParams(LocalDate startDate, LocalDate endDate, double latitude,
		double longitude) {
		return getDefaultParams("uv_index_max", startDate, endDate, latitude, longitude);
	}

	private MultiValueMap<String, String> getDefaultParams(String daily, LocalDate startDate, LocalDate endDate,
		double latitude,
		double longitude
	) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("latitude", String.valueOf(latitude));
		params.add("longitude", String.valueOf(longitude));
		params.add("daily", daily);
		params.add("timezone", timezone);
		params.add("start_date", startDate.toString());
		params.add("end_date", endDate.toString());
		return params;
	}
}
