package sevenstar.marineleisure.spot.dto.detail.items;

import java.time.LocalDate;

import lombok.Getter;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivitySpotDetail;

@Getter
public class MudflatSpotDetail implements ActivitySpotDetail {
	private final LocalDate forecastDate;
	private final String startTime;
	private final String endTime;
	private final RangeDetail airTemp;
	private final RangeDetail windSpeed;
	private final String weather;
	private final TotalIndex totalIndex;
	private final int uvIndex;

	public MudflatSpotDetail(LocalDate forecastDate, String startTime, String endTime, RangeDetail airTemp,
		RangeDetail windSpeed, String weather, TotalIndex totalIndex, int uvIndex) {
		this.forecastDate = forecastDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.airTemp = airTemp;
		this.windSpeed = windSpeed;
		this.weather = weather;
		this.totalIndex = totalIndex;
		this.uvIndex = uvIndex;
	}
}