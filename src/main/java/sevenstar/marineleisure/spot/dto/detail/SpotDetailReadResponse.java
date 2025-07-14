package sevenstar.marineleisure.spot.dto.detail;

import java.time.LocalDate;
import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

public record SpotDetailReadResponse<T>(
	Long spotId,
	String name,
	ActivityCategory category,
	float latitude,
	float longitude,
	boolean isFavorite,
	List<T> detail
) {

	public record FishingSpotDetail(
		LocalDate forecastDate,
		TimePeriod timePeriod,
		String tide,
		TotalIndex totalIndex,
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
		LocalDate forecastDate,
		TimePeriod timePeriod,
		float waveHeight,
		int wavePeriod,
		float windSpeed,
		float seaTemp,
		TotalIndex totalIndex,
		int uvIndex
	) {
	}

	public record ScubaSpotDetail(
		LocalDate forecastDate,
		TimePeriod timePeriod,
		String sunrise,
		String sunset,
		String tide,
		RangeDetail waveHeight,
		RangeDetail seaTemp,
		RangeDetail currentSpeed,
		TotalIndex totalIndex
	) {

	}

	public record MudflatSpotDetail(
		String forecastDate,
		String startTime,
		String endTime,
		RangeDetail airTemp,
		RangeDetail windSpeed,
		String weather,
		TotalIndex totalIndex,
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
