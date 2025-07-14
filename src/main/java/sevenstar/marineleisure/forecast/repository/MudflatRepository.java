package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.global.enums.TotalIndex;

public interface MudflatRepository extends JpaRepository<Mudflat, Long> {
	@Query(value = """
					SELECT DISTINCT m.spotId FROM Mudflat m
					WHERE m.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	Optional<Mudflat> findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	@Query("""
		SELECT m.totalIndex
		FROM Mudflat m
		WHERE m.spotId = :spotId AND m.forecastDate = :date
		""")
	Optional<TotalIndex> findTotalIndexBySpotIdAndDate(@Param("spotId") Long spotId, @Param("date") LocalDate date);

	@Modifying
	@Transactional
	@Query(value = """
		INSERT INTO mudflat_forecast (
		    spot_id, forecast_date, start_time, end_time, 
		    air_temp_min, air_temp_max, wind_speed_min, wind_speed_max,
		    weather, total_index, created_at, updated_at
		) VALUES (
		    :spotId, :forecastDate, :startTime, :endTime,
		    :airTempMin, :airTempMax, :windSpeedMin, :windSpeedMax,
		    :weather, :totalIndex, NOW(), NOW()
		)
		ON DUPLICATE KEY UPDATE
		    start_time = VALUES(start_time),
		    end_time = VALUES(end_time),
		    air_temp_min = VALUES(air_temp_min),
		    air_temp_max = VALUES(air_temp_max),
		    wind_speed_min = VALUES(wind_speed_min),
		    wind_speed_max = VALUES(wind_speed_max),
		    weather = VALUES(weather),
		    total_index = VALUES(total_index),
		    updated_at = NOW()
		""", nativeQuery = true)
	void upsertMudflat(
		@Param("spotId") Long spotId,
		@Param("forecastDate") LocalDate forecastDate,
		@Param("startTime") LocalTime startTime,
		@Param("endTime") LocalTime endTime,
		@Param("airTempMin") Float airTempMin,
		@Param("airTempMax") Float airTempMax,
		@Param("windSpeedMin") Float windSpeedMin,
		@Param("windSpeedMax") Float windSpeedMax,
		@Param("weather") String weather,
		@Param("totalIndex") String totalIndex
	);

	@Modifying
	@Transactional
	@Query("""
		    UPDATE Mudflat m
		    SET m.uvIndex = :uvIndex
		    WHERE m.spotId = :spotId
		      AND m.forecastDate = :forecastDate
		""")
	void updateUvIndex(
		@Param("uvIndex") Float uvIndex,
		@Param("spotId") Long spotId,
		@Param("forecastDate") LocalDate forecastDate
	);

}