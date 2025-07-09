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
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "surfing_forecast", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"spot_id", "forecast_date", "time_period"})})
public class Surfing extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(name = "forecast_date", nullable = false)
	private LocalDate forecastDate;

	@Column(name = "time_period", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private TimePeriod timePeriod;

	@Column(name = "wave_height")
	private Float waveHeight;

	@Column(name = "wave_period")
	private Float wavePeriod;

	@Column(name = "wind_speed")
	private Float windSpeed;

	@Column(name = "sea_temp")
	private Float seaTemp;

	@Column(name = "total_index")
	private TotalIndex totalIndex;

	@Column(name = "uv_index")
	private Float uvIndex;

	@Builder
	public Surfing(Long spotId, LocalDate forecastDate, TimePeriod timePeriod, Float waveHeight, Float wavePeriod,
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

	public void updateUvIndex(Float uvIndex) {
		this.uvIndex = uvIndex;
	}
}