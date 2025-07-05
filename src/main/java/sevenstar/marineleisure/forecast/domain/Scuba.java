package sevenstar.marineleisure.forecast.domain;

import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "scuba_forecast")
public class Scuba extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(name = "forecast_date", nullable = false)
	private LocalDate forecastDate;

	@Column(name = "time_period", length = 10, nullable = false)
	private String timePeriod;

	private LocalTime sunrise;
	private LocalTime sunset;

	@Column(columnDefinition = "TEXT")
	private String tide;

	private TotalIndex totalIndex;

	private Float waveHeightMin;
	private Float waveHeightMax;
	private Float seaTempMin;
	private Float seaTempMax;
	private Float currentSpeedMin;
	private Float currentSpeedMax;

	@Builder

	public Scuba(Long spotId, LocalDate forecastDate, String timePeriod, LocalTime sunrise, LocalTime sunset,
		String tide,
		TotalIndex totalIndex, Float waveHeightMin, Float waveHeightMax, Float seaTempMin, Float seaTempMax,
		Float currentSpeedMin, Float currentSpeedMax) {
		this.spotId = spotId;
		this.forecastDate = forecastDate;
		this.timePeriod = timePeriod;
		this.sunrise = sunrise;
		this.sunset = sunset;
		this.tide = tide;
		this.totalIndex = totalIndex;
		this.waveHeightMin = waveHeightMin;
		this.waveHeightMax = waveHeightMax;
		this.seaTempMin = seaTempMin;
		this.seaTempMax = seaTempMax;
		this.currentSpeedMin = currentSpeedMin;
		this.currentSpeedMax = currentSpeedMax;
	}
}
