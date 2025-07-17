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

	public FishingSpotDetail(LocalDate forecastDate, TimePeriod timePeriod, String tide, TotalIndex totalIndex,
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
}
