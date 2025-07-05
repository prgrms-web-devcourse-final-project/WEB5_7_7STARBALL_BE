package sevenstar.marineleisure.forecast.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "surfing_forecast")
public class Surfing extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(name = "forecast_date", nullable = false)
	private LocalDate forecastDate;

	@Column(name = "time_period", length = 10, nullable = false)
	private String timePeriod;

	private Float waveHeight;
	private Float wavePeriod;
	private Float windSpeed;
	private Float seaTemp;

	private TotalIndex totalIndex;

	private Float uvIndex;

	@Builder
	public Surfing(Long spotId, LocalDate forecastDate, String timePeriod, Float waveHeight, Float wavePeriod,
		Float windSpeed, Float seaTemp, TotalIndex totalIndex, Float uvIndex) {
		this.spotId = spotId;
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