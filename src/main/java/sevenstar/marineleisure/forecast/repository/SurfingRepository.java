package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.global.enums.TimePeriod;

public interface SurfingRepository extends JpaRepository<Surfing, Long> {
	boolean existsBySpotIdAndForecastDateAndTimePeriod(Long spotId, LocalDate forecastDate, TimePeriod timePeriod);

	@Query(value = """
					SELECT DISTINCT s.spotId FROM Surfing s
					WHERE s.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	List<Surfing> findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	@Query("""
		    SELECT s FROM Surfing s
		    WHERE s.spotId = :spotId
				AND s.timePeriod != :exceptTimePeriod
		    	AND s.forecastDate = :date
		""")
	Optional<Surfing> findFishingForecasts(@Param("spotId") Long spotId, @Param("date") LocalDate date,
		@Param("exceptTimePeriod") TimePeriod exceptTimePeriod);
}