package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.global.enums.TimePeriod;

public interface FishingRepository extends JpaRepository<Fishing, Long> {
	@Query(value = """
					SELECT DISTINCT f.spotId FROM Fishing f
					WHERE f.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	@Query("""
		    SELECT f FROM Fishing f
		    WHERE f.spotId = :spotId
				AND f.timePeriod != :exceptTimePeriod
		    	AND f.forecastDate = :date
		""")
	Optional<Fishing> findFishingForecasts(@Param("spotId") Long spotId, @Param("date") LocalDate date,
		@Param("exceptTimePeriod") TimePeriod exceptTimePeriod);

	@Modifying
	@Transactional
	@Query(value = """
		INSERT INTO fishing_forecast (
		    spot_id, target_id, forecast_date, time_period, tide, total_index,
		    wave_height_min, wave_height_max, sea_temp_min, sea_temp_max,
		    air_temp_min, air_temp_max, current_speed_min, current_speed_max,
		    wind_speed_min, wind_speed_max, created_at, updated_at
		) VALUES (
		    :spotId, :targetId, :forecastDate, :timePeriod, :tide, :totalIndex,
		    :waveHeightMin, :waveHeightMax, :seaTempMin, :seaTempMax,
		    :airTempMin, :airTempMax, :currentSpeedMin, :currentSpeedMax,
		    :windSpeedMin, :windSpeedMax, NOW(), NOW()
		)
		ON DUPLICATE KEY UPDATE
		    tide = VALUES(tide),
		    total_index = VALUES(total_index),
		    wave_height_min = VALUES(wave_height_min),
		    wave_height_max = VALUES(wave_height_max),
		    sea_temp_min = VALUES(sea_temp_min),
		    sea_temp_max = VALUES(sea_temp_max),
		    air_temp_min = VALUES(air_temp_min),
		    air_temp_max = VALUES(air_temp_max),
		    current_speed_min = VALUES(current_speed_min),
		    current_speed_max = VALUES(current_speed_max),
		    wind_speed_min = VALUES(wind_speed_min),
		    wind_speed_max = VALUES(wind_speed_max),
		    updated_at = NOW()
		""", nativeQuery = true)
	void upsertFishing(
		@Param("spotId") Long spotId,
		@Param("targetId") Long targetId,
		@Param("forecastDate") LocalDate forecastDate,
		@Param("timePeriod") String timePeriod,
		@Param("tide") String tide,
		@Param("totalIndex") String totalIndex,
		@Param("waveHeightMin") Float waveHeightMin,
		@Param("waveHeightMax") Float waveHeightMax,
		@Param("seaTempMin") Float seaTempMin,
		@Param("seaTempMax") Float seaTempMax,
		@Param("airTempMin") Float airTempMin,
		@Param("airTempMax") Float airTempMax,
		@Param("currentSpeedMin") Float currentSpeedMin,
		@Param("currentSpeedMax") Float currentSpeedMax,
		@Param("windSpeedMin") Float windSpeedMin,
		@Param("windSpeedMax") Float windSpeedMax
	);

	@Modifying
	@Transactional
	@Query("""
		    UPDATE Fishing f
		    SET f.uvIndex = :uvIndex
		    WHERE f.spotId = :spotId
		      AND f.forecastDate = :forecastDate
		""")
	void updateUvIndex(
		@Param("uvIndex") Float uvIndex,
		@Param("spotId") Long spotId,
		@Param("forecastDate") LocalDate forecastDate
	);

}
