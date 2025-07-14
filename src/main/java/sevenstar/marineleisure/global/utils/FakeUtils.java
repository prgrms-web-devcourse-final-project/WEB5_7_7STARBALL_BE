package sevenstar.marineleisure.global.utils;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.FishingTarget;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

/**
 * 외부 API 데이터를 수급하기 때문에 만약 수급 과정에서 누락이 발생할 경우 유연한 대처를 위한 fake 객체 리턴
 * @author gunwoong
 */
@UtilityClass
public class FakeUtils {
	public static Fishing fakeFishing(Long spotId) {
		return Fishing.builder()
			.spotId(spotId)
			.targetId(-1L)
			.forecastDate(LocalDate.now())
			.timePeriod(TimePeriod.AM)
			.tide(TidePhase.Intermediate_Tide)
			.totalIndex(TotalIndex.NORMAL)
			.waveHeightMin(0F)
			.waveHeightMax(0F)
			.seaTempMin(0F)
			.seaTempMax(0F)
			.airTempMin(0F)
			.airTempMax(0F)
			.currentSpeedMin(0F)
			.currentSpeedMax(0F)
			.windSpeedMin(0F)
			.windSpeedMax(0F)
			.uvIndex(0F)
			.build();
	}

	public static FishingTarget fakeFishingTarget() {
		return new FishingTarget("");
	}

	public static Surfing fakeSurfing(Long spotId) {
		return Surfing.builder()
			.spotId(spotId)
			.forecastDate(LocalDate.now())
			.timePeriod(TimePeriod.AM)
			.waveHeight(0F)
			.wavePeriod(0F)
			.windSpeed(0F)
			.seaTemp(0F)
			.totalIndex(TotalIndex.NORMAL)
			.uvIndex(0F)
			.build();
	}

	public static Scuba fakeScuba(Long spotId) {
		return Scuba.builder()
			.spotId(spotId)
			.forecastDate(LocalDate.now())
			.timePeriod(TimePeriod.AM)
			.tide(TidePhase.Intermediate_Tide)
			.totalIndex(TotalIndex.NORMAL)
			.waveHeightMin(0F)
			.waveHeightMax(0F)
			.seaTempMin(0F)
			.seaTempMax(0F)
			.currentSpeedMin(0F)
			.currentSpeedMax(0F)
			.sunrise(LocalTime.now())
			.sunset(LocalTime.now())
			.build();
	}

	public static Mudflat fakeMudflat(Long spotId) {
		return Mudflat.builder()
			.spotId(spotId)
			.forecastDate(LocalDate.now())
			.startTime(LocalTime.now())
			.endTime(LocalTime.now())
			.airTempMin(0F)
			.airTempMax(0F)
			.windSpeedMin(0F)
			.windSpeedMax(0F)
			.weather("")
			.totalIndex(TotalIndex.NORMAL)
			.uvIndex(0F)
			.build();

	}
}
