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
@Table(name = "mudflat_forecast")
public class Mudflat extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(name = "forecast_date", nullable = false)
	private LocalDate forecastDate;

	@Column(name = "start_time")
	private LocalTime startTime;

	@Column(name = "end_time")
	private LocalTime endTime;

	@Column(name = "uv_index")
	private Float uvIndex;

	@Column(name = "air_temp_min")
	private Float airTempMin;

	@Column(name = "air_temp_max")
	private Float airTempMax;

	@Column(name = "wind_speed_min")
	private Float windSpeedMin;

	@Column(name = "wind_speed_max")
	private Float windSpeedMax;

	@Column(name = "weather")
	private String weather;

	@Column(name = "total_index")
	private TotalIndex totalIndex;

	@Builder
	public Mudflat(Long spotId, LocalDate forecastDate, LocalTime startTime, LocalTime endTime, Float uvIndex,
		Float airTempMin, Float airTempMax, Float windSpeedMin, Float windSpeedMax, String weather,
		TotalIndex totalIndex) {
		this.spotId = spotId;
		this.forecastDate = forecastDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.uvIndex = uvIndex;
		this.airTempMin = airTempMin;
		this.airTempMax = airTempMax;
		this.windSpeedMin = windSpeedMin;
		this.windSpeedMax = windSpeedMax;
		this.weather = weather;
		this.totalIndex = totalIndex;
	}
}