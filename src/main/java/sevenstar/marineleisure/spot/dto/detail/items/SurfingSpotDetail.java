package sevenstar.marineleisure.spot.dto.detail.items;

import java.time.LocalDate;

import lombok.Getter;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivitySpotDetail;

@Getter
public class SurfingSpotDetail implements ActivitySpotDetail {
	private final LocalDate forecastDate;
	private final TimePeriod timePeriod;
	private final float waveHeight;
	private final int wavePeriod;
	private final float windSpeed;
	private final float seaTemp;
	private final TotalIndex totalIndex;
	private final int uvIndex;

	public SurfingSpotDetail(LocalDate forecastDate, TimePeriod timePeriod, float waveHeight, int wavePeriod,
		float windSpeed, float seaTemp, TotalIndex totalIndex, int uvIndex) {
		this.forecastDate = forecastDate;
		this.timePeriod = timePeriod;
		this.waveHeight = waveHeight;
		this.wavePeriod = wavePeriod;
		this.windSpeed = windSpeed;
		this.seaTemp = seaTemp;
		this.totalIndex = totalIndex;
		this.uvIndex = uvIndex;
	}


}