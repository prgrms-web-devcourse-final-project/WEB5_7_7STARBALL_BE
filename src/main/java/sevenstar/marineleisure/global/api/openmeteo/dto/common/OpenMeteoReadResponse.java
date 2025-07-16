package sevenstar.marineleisure.global.api.openmeteo.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class OpenMeteoReadResponse<T> {
	private double latitude;
	private double longitude;
	@JsonProperty("generationtime_ms")
	private double generationtimeMs;
	@JsonProperty("utc_offset_seconds")
	private int utcOffsetSeconds;
	private String timezone;
	@JsonProperty("timezone_abbreviation")
	private String timezoneAbbreviation;
	private int elevation;
	@JsonProperty("daily_units")
	private DailyUnits dailyUnits;
	private T daily;

	public OpenMeteoReadResponse(double latitude, double longitude, double generationtimeMs, int utcOffsetSeconds,
		String timezone, String timezoneAbbreviation, int elevation, DailyUnits dailyUnits, T daily) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.generationtimeMs = generationtimeMs;
		this.utcOffsetSeconds = utcOffsetSeconds;
		this.timezone = timezone;
		this.timezoneAbbreviation = timezoneAbbreviation;
		this.elevation = elevation;
		this.dailyUnits = dailyUnits;
		this.daily = daily;
	}

	@Getter
	public static class DailyUnits {
		private String time;
		private String sunrise;
		private String sunset;
		private String uvIndexMax;

		public DailyUnits(String time, String sunrise, String sunset, String uvIndexMax) {
			this.time = time;
			this.sunrise = sunrise;
			this.sunset = sunset;
			this.uvIndexMax = uvIndexMax;
		}
	}
}
