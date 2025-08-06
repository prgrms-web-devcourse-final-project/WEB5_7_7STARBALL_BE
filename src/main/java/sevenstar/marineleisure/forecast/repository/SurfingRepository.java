package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

public interface SurfingRepository extends ActivityRepository<Surfing, Long> {
	@Query(value = """
					SELECT DISTINCT s.spotId FROM Surfing s
					WHERE s.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	@Query("""
		    SELECT s FROM Surfing s
		    WHERE s.spotId = :spotId
		    	AND s.forecastDate = :date
		""")
	List<Surfing> findSurfingForecasts(@Param("spotId") Long spotId, @Param("date") LocalDate date);

	@Modifying
	@Transactional
	@Query(value = """
		INSERT INTO surfing_forecast (
		    spot_id, forecast_date, time_period, wave_height, wave_period,
		    wind_speed, sea_temp, total_index, created_at, updated_at
		) VALUES (
		    :spotId, :forecastDate, :timePeriod, :waveHeight, :wavePeriod,
		    :windSpeed, :seaTemp, :totalIndex, NOW(), NOW()
		)
		ON DUPLICATE KEY UPDATE
		    wave_height = VALUES(wave_height),
		    wave_period = VALUES(wave_period),
		    wind_speed = VALUES(wind_speed),
		    sea_temp = VALUES(sea_temp),
		    total_index = VALUES(total_index),
		    updated_at = NOW()
		""", nativeQuery = true)
	void upsertSurfing(
		@Param("spotId") Long spotId,
		@Param("forecastDate") LocalDate forecastDate,
		@Param("timePeriod") String timePeriod,
		@Param("waveHeight") Float waveHeight,
		@Param("wavePeriod") Float wavePeriod,
		@Param("windSpeed") Float windSpeed,
		@Param("seaTemp") Float seaTemp,
		@Param("totalIndex") String totalIndex
	);

	@Modifying
	@Transactional
	@Query("""
		    UPDATE Surfing s
		    SET s.uvIndex = :uvIndex
		    WHERE s.spotId = :spotId
		      AND s.forecastDate = :forecastDate
		""")
	void updateUvIndex(
		@Param("uvIndex") Float uvIndex,
		@Param("spotId") Long spotId,
		@Param("forecastDate") LocalDate forecastDate
	);

	Optional<Surfing> findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		Long spotId,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	);

	@Query(value = """
        SELECT *
        FROM surfing_forecast s
        WHERE s.forecast_date = :forecastDate
        ORDER BY s.total_index DESC
        LIMIT 1
        """,nativeQuery = true)
	Optional<Surfing> findBestTotaIndexSurfing(@Param("forecastDate") LocalDate forecastDate);

	Optional<Surfing> findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(LocalDateTime start, LocalDateTime end);

	Optional<Surfing> findBySpotIdAndCreatedAtBeforeOrderByCreatedAtDesc(Long spotId, LocalDateTime createdAtBefore);

	Optional<Surfing> findBySpotIdOrderByCreatedAt(Long spotId);

	Optional<Surfing> findBySpotIdAndForecastDateAndTimePeriod(Long spotId, LocalDate forecastDate, TimePeriod timePeriod);
}