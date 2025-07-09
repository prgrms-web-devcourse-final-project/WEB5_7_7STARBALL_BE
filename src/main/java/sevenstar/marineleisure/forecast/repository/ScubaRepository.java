package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.global.enums.TimePeriod;

public interface ScubaRepository extends JpaRepository<Scuba, Long> {
	boolean existsBySpotIdAndForecastDateAndTimePeriod(Long spotId, LocalDate forecastDate, TimePeriod timePeriod);

	@Query(value = """
					SELECT DISTINCT s.spotId FROM Scuba s
					WHERE s.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	List<Scuba>  findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	@Query("""
		    SELECT s FROM Scuba s
		    WHERE s.spotId = :spotId
				AND s.timePeriod != :exceptTimePeriod
		    	AND s.forecastDate = :date
		""")
	Optional<Scuba> findFishingForecasts(@Param("spotId") Long spotId, @Param("date") LocalDate date,
		@Param("exceptTimePeriod") TimePeriod exceptTimePeriod);
}