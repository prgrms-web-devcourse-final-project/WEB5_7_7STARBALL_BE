package sevenstar.marineleisure.spot.dto;

import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;

public record SpotDetailReadResponse<T>(
	Long id,
	String name,
	ActivityCategory category,
	String location,
	float latitude,
	float longitude,
	boolean isFavorite,
	List<T> detail
) {

	public record FishingSpotDetail(
		String forecastDate,
		String timePeriod,
		String tide,
		String totalIndex,
		RangeDetail waveHeight,
		RangeDetail seaTemp,
		RangeDetail airTemp,
		RangeDetail currentSpeed,
		RangeDetail windSpeed,
		int uvIndex,
		FishDetail target
	) {
	}

	public record SurfingSpotDetail(
		String forecastDate,
		String timePeriod,
		float waveHeight,
		int wavePeriod,
		float windSpeed,
		float seaTemp,
		String totalIndex,
		int uvIndex
	) {
	}

	public record ScubaSpotDetail(
		String forecastDate,
		String timePeriod,
		String sunrise,
		String sunset,
		String tide,
		RangeDetail waveHeight,
		RangeDetail seaTemp,
		RangeDetail currentSpeed,
		String totalIndex
	) {

	}

	public record MudflatSpotDetail(
		String forecastDate,
		String startTime,
		String endTime,
		RangeDetail airTemp,
		RangeDetail windSpeed,
		String weather,
		String totalIndex,
		int uvIndex
	) {

	}

	public record RangeDetail(
		float min,
		float max
	) {

	}

	public record FishDetail(
		Long id,
		String name
	) {

	}

}
