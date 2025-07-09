package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.global.enums.TimePeriod;

public interface FishingRepository extends JpaRepository<Fishing, Long> {
	boolean existsBySpotIdAndForecastDateAndTimePeriod(Long spotId, LocalDate forecastDate, TimePeriod timePeriod);

	@Query(value = """
					SELECT DISTINCT f.spotId FROM Fishing f
					WHERE f.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	List<Fishing> findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	@Query("""
		    SELECT f FROM Fishing f
		    WHERE f.spotId = :spotId
				AND f.timePeriod != :exceptTimePeriod
		    	AND f.forecastDate = :date
		""")
	Optional<Fishing> findFishingForecasts(@Param("spotId") Long spotId, @Param("date") LocalDate date,
		@Param("exceptTimePeriod") TimePeriod exceptTimePeriod);

}
