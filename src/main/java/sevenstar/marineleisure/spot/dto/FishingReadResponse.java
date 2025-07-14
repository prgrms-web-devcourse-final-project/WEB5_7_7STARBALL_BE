package sevenstar.marineleisure.spot.dto;

import java.time.LocalDate;

import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

public record FishingReadResponse(
	Long spotId,
	Long targetId,
	String targetName,
	LocalDate forecastDate,
	TimePeriod timePeriod,
	TidePhase tide,
	TotalIndex totalIndex,
	Float waveHeightMin,
	Float waveHeightMax,
	Float seaTempMin,
	Float seaTempMax,
	Float airTempMin,
	Float airTempMax,
	Float currentSpeedMin,
	Float currentSpeedMax,
	Float windSpeedMin,
	Float windSpeedMax,
	Float uvIndex
) {
}
