package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.forecast.domain.Scuba;

public interface ScubaRepository extends JpaRepository<Scuba, Long> {
	boolean existsBySpotIdAndForecastDateAndTimePeriod(Long spotId, LocalDate forecastDate, String timePeriod);

	@Query(value = """
					SELECT DISTINCT s.spotId FROM Scuba s
					WHERE s.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	List<Scuba>  findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);


}