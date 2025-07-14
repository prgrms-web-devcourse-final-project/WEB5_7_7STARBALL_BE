package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;

public interface ScubaRepository extends JpaRepository<Scuba, Long> {
	@Query(value = """
					SELECT DISTINCT s.spotId FROM Scuba s
					WHERE s.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	@Query("""
		    SELECT s FROM Scuba s
		    WHERE s.spotId = :spotId
		    	AND s.forecastDate = :date
		""")
	List<Scuba> findScubaForecasts(@Param("spotId") Long spotId, @Param("date") LocalDate date);

	@Query("""
		SELECT s.totalIndex
		FROM Scuba s
		WHERE s.spotId = :spotId AND s.forecastDate = :date AND s.timePeriod = :timePeriod
		""")
	Optional<TotalIndex> findTotalIndexBySpotIdAndDate(@Param("spotId") Long spotId, @Param("date") LocalDate date,@Param("timePeriod") TimePeriod timePeriod);

	Optional<Scuba> findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		Long spotId,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime);
  
	@Modifying
	@Transactional
	@Query(value = """
		INSERT INTO scuba_forecast (
		    spot_id, forecast_date, time_period, tide, total_index,
		    wave_height_min, wave_height_max, sea_temp_min, sea_temp_max,
		    current_speed_min, current_speed_max, created_at, updated_at
		) VALUES (
		    :spotId, :forecastDate, :timePeriod, :tide, :totalIndex,
		    :waveHeightMin, :waveHeightMax, :seaTempMin, :seaTempMax,
		    :currentSpeedMin, :currentSpeedMax, NOW(), NOW()
		)
		ON DUPLICATE KEY UPDATE
		    tide = VALUES(tide),
		    total_index = VALUES(total_index),
		    wave_height_min = VALUES(wave_height_min),
		    wave_height_max = VALUES(wave_height_max),
		    sea_temp_min = VALUES(sea_temp_min),
		    sea_temp_max = VALUES(sea_temp_max),
		    current_speed_min = VALUES(current_speed_min),
		    current_speed_max = VALUES(current_speed_max),
		    updated_at = NOW()
		""", nativeQuery = true)
	void upsertScuba(
		@Param("spotId") Long spotId,
		@Param("forecastDate") LocalDate forecastDate,
		@Param("timePeriod") String timePeriod,
		@Param("tide") String tide,
		@Param("totalIndex") String totalIndex,
		@Param("waveHeightMin") Float waveHeightMin,
		@Param("waveHeightMax") Float waveHeightMax,
		@Param("seaTempMin") Float seaTempMin,
		@Param("seaTempMax") Float seaTempMax,
		@Param("currentSpeedMin") Float currentSpeedMin,
		@Param("currentSpeedMax") Float currentSpeedMax
	);

	@Modifying
	@Transactional
	@Query("""
		    UPDATE Scuba s
		    SET s.sunrise = :sunrise,
		        s.sunset = :sunset
		    WHERE s.spotId = :spotId
		      AND s.forecastDate = :forecastDate
		""")
	void updateSunriseAndSunset(
		@Param("sunrise") LocalTime sunrise,
		@Param("sunset") LocalTime sunset,
		@Param("spotId") Long spotId,
		@Param("forecastDate") LocalDate forecastDate
	);

	Optional<Scuba> findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(LocalDateTime start, LocalDateTime end);

	Optional<Scuba> findBySpotIdAndCreatedAtBeforeOrderByCreatedAtDesc(Long spotId, LocalDateTime createdAtBefore);

	// Optional<Scuba> findBySpotIdOrderByCreatedAt(Long spotId);
}