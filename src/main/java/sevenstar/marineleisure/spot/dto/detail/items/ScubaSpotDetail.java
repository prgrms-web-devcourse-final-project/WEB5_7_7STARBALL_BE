package sevenstar.marineleisure.spot.dto.detail.items;

import java.time.LocalDate;

import lombok.Getter;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivitySpotDetail;

@Getter
public class ScubaSpotDetail implements ActivitySpotDetail {
	private final LocalDate forecastDate;
	private final TimePeriod timePeriod;
	private final String sunrise;
	private final String sunset;
	private final String tide;
	private final RangeDetail waveHeight;
	private final RangeDetail seaTemp;
	private final RangeDetail currentSpeed;
	private final TotalIndex totalIndex;

	public ScubaSpotDetail(LocalDate forecastDate, TimePeriod timePeriod, String sunrise, String sunset, String tide,
		RangeDetail waveHeight, RangeDetail seaTemp, RangeDetail currentSpeed, TotalIndex totalIndex) {
		this.forecastDate = forecastDate;
		this.timePeriod = timePeriod;
		this.sunrise = sunrise;
		this.sunset = sunset;
		this.tide = tide;
		this.waveHeight = waveHeight;
		this.seaTemp = seaTemp;
		this.currentSpeed = currentSpeed;
		this.totalIndex = totalIndex;
	}

}