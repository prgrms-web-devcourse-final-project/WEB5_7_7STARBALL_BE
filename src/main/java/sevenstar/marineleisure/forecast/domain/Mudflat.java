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

	private LocalTime startTime;
	private LocalTime endTime;

	private Float uvIndex;
	private Float airTempMin;
	private Float airTempMax;
	private Float windSpeedMin;
	private Float windSpeedMax;

	private String weather;

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