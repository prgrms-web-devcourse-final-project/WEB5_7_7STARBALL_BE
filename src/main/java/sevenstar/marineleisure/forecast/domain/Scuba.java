package sevenstar.marineleisure.forecast.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "scuba_forecast", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"spot_id", "forecast_date", "time_period"})})
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

	@Column(name = "tide")
	@Enumerated(EnumType.STRING)
	private TidePhase tide;

	@Column(name = "total_index")
	private TotalIndex totalIndex;

	@Column(name = "wave_height_min")
	private Float waveHeightMin;

	@Column(name = "wave_height_max")
	private Float waveHeightMax;

	@Column(name = "sea_temp_min")
	private Float seaTempMin;

	@Column(name = "sea_temp_max")
	private Float seaTempMax;

	@Column(name = "current_speed_min")
	private Float currentSpeedMin;

	@Column(name = "current_speed_max")
	private Float currentSpeedMax;

	@Builder
	public Scuba(Long spotId, LocalDate forecastDate, String timePeriod, LocalTime sunrise, LocalTime sunset,
		TidePhase tide, TotalIndex totalIndex, Float waveHeightMin, Float waveHeightMax, Float seaTempMin,
		Float seaTempMax, Float currentSpeedMin, Float currentSpeedMax) {
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

	public void updateSunriseAndSunset(LocalTime sunrise, LocalTime sunset) {
		this.sunrise = sunrise;
		this.sunset = sunset;
	}
}
