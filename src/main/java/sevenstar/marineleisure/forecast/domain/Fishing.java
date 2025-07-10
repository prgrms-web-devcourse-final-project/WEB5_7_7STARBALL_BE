package sevenstar.marineleisure.forecast.domain;

import java.time.LocalDate;

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
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "fishing_forecast", uniqueConstraints = @UniqueConstraint(columnNames = {"spot_id", "forecast_date",
	"time_period"}))
public class Fishing extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(name = "target_id")
	private Long targetId;

	@Column(name = "forecast_date", nullable = false)
	private LocalDate forecastDate;

	@Column(name = "time_period", length = 10)
	@Enumerated(EnumType.STRING)
	private TimePeriod timePeriod;

	@Column(name = "tide")
	@Enumerated(EnumType.STRING)
	private TidePhase tide;

	@Column(name = "total_index")
	@Enumerated(EnumType.STRING)
	private TotalIndex totalIndex;

	@Column(name = "wave_height_min")
	private Float waveHeightMin;

	@Column(name = "wave_height_max")
	private Float waveHeightMax;

	@Column(name = "sea_temp_min")
	private Float seaTempMin;

	@Column(name = "sea_temp_max")
	private Float seaTempMax;

	@Column(name = "air_temp_min")
	private Float airTempMin;

	@Column(name = "air_temp_max")
	private Float airTempMax;

	@Column(name = "current_speed_min")
	private Float currentSpeedMin;

	@Column(name = "current_speed_max")
	private Float currentSpeedMax;

	@Column(name = "wind_speed_min")
	private Float windSpeedMin;

	@Column(name = "wind_speed_max")
	private Float windSpeedMax;

	@Column(name = "uv_index")
	private Float uvIndex;

	@Builder(toBuilder = true)
	public Fishing(Long spotId, Long targetId, LocalDate forecastDate, TimePeriod timePeriod, TidePhase tide,
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

	public void updateUvIndex(Float uvIndex) {
		this.uvIndex = uvIndex;
	}
}