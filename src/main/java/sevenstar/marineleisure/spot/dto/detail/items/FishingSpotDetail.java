package sevenstar.marineleisure.spot.dto.detail.items;

import java.time.LocalDate;

import lombok.Getter;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivitySpotDetail;
import sevenstar.marineleisure.spot.dto.projection.FishingReadProjection;

@Getter
public class FishingSpotDetail implements ActivitySpotDetail {

	private LocalDate forecastDate;
	private TimePeriod timePeriod;
	private String tide;
	private TotalIndex totalIndex;
	private RangeDetail waveHeight;
	private RangeDetail seaTemp;
	private RangeDetail airTemp;
	private RangeDetail currentSpeed;
	private RangeDetail windSpeed;
	private int uvIndex;
	private FishDetail target;

	private FishingSpotDetail(LocalDate forecastDate, TimePeriod timePeriod, String tide, TotalIndex totalIndex,
		RangeDetail waveHeight, RangeDetail seaTemp, RangeDetail airTemp, RangeDetail currentSpeed,
		RangeDetail windSpeed,
		int uvIndex, FishDetail target) {
		this.forecastDate = forecastDate;
		this.timePeriod = timePeriod;
		this.tide = tide;
		this.totalIndex = totalIndex;
		this.waveHeight = waveHeight;
		this.seaTemp = seaTemp;
		this.airTemp = airTemp;
		this.currentSpeed = currentSpeed;
		this.windSpeed = windSpeed;
		this.uvIndex = uvIndex;
		this.target = target;
	}

	public static FishingSpotDetail of(FishingReadProjection projection) {
		return new FishingSpotDetail(projection.getForecastDate(), projection.getTimePeriod(),
			projection.getTide().getDescription(),
			projection.getTotalIndex(),
			RangeDetail.of(projection.getWaveHeightMin(), projection.getWaveHeightMax()),
			RangeDetail.of(projection.getSeaTempMin(), projection.getSeaTempMax()),
			RangeDetail.of(projection.getAirTempMin(), projection.getAirTempMax()),
			RangeDetail.of(projection.getCurrentSpeedMin(), projection.getCurrentSpeedMax()),
			RangeDetail.of(projection.getWindSpeedMin(), projection.getWindSpeedMax()),
			projection.getUvIndex().intValue(), new FishDetail(projection.getTargetId(), projection.getTargetName()));
	}
}
