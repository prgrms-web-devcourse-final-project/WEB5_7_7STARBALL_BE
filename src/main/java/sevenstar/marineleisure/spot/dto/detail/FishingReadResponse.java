package sevenstar.marineleisure.spot.dto.detail;

import java.time.LocalDate;

import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

public class FishingReadResponse implements ActivityDetailResponse {
	private Long spotId;
	private Long targetI;
	private String targetName;
	private LocalDate forecastDate;
	private TimePeriod timePeriod;
	private TidePhase tide;
	private TotalIndex totalIndex;
	private Float waveHeightMin;
	private Float waveHeightMax;
	private Float seaTempMin;
	private Float seaTempMax;
	private Float airTempMin;
	private Float airTempMax;
	private Float currentSpeedMin;
	private Float currentSpeedMax;
	private Float windSpeedMin;
	private Float windSpeedMax;
	private Float uvIndex;
}
