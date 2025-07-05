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
@Table(name = "fishing_forecast")
public class Fishing extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(name = "target_id", nullable = false)
	private Long targetId;

	@Column(name = "forecast_date", nullable = false)
	private LocalDate forecastDate;

	@Column(name = "time_period", length = 10)
	private String timePeriod;

	private Integer tide;

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

	@Builder
	public Fishing(Long spotId, Long targetId, LocalDate forecastDate, String timePeriod, Integer tide,
		TotalIndex totalIndex, Float waveHeightMin, Float waveHeightMax, Float seaTempMin, Float seaTempMax,
		Float airTempMin, Float airTempMax, Float currentSpeedMin, Float currentSpeedMax, Float windSpeedMin,
		Float windSpeedMax, Float uvIndex) {
		this.spotId = spotId;
		this.targetId = targetId;
		this.forecastDate = forecastDate;
		this.timePeriod = timePeriod;
		this.tide = tide;
		this.totalIndex = totalIndex;
		this.waveHeightMin = waveHeightMin;
		this.waveHeightMax = waveHeightMax;
		this.seaTempMin = seaTempMin;
		this.seaTempMax = seaTempMax;
		this.airTempMin = airTempMin;
		this.airTempMax = airTempMax;
		this.currentSpeedMin = currentSpeedMin;
		this.currentSpeedMax = currentSpeedMax;
		this.windSpeedMin = windSpeedMin;
		this.windSpeedMax = windSpeedMax;
		this.uvIndex = uvIndex;
	}
}