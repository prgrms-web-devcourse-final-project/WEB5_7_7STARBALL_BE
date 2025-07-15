package sevenstar.marineleisure.spot.dto.projection;

import java.time.LocalDate;

import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

public interface FishingReadProjection {
	LocalDate getForecastDate();
	TimePeriod getTimePeriod();
	TidePhase getTide();
	TotalIndex getTotalIndex();
	Float getWaveHeightMin();
	Float getWaveHeightMax();
	Float getSeaTempMin();
	Float getSeaTempMax();
	Float getAirTempMin();
	Float getAirTempMax();
	Float getCurrentSpeedMin();
	Float getCurrentSpeedMax();
	Float getWindSpeedMin();
	Float getWindSpeedMax();
	Float getUvIndex();
	Long getTargetId();
	String getTargetName();
}
