package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.forecast.domain.Fishing;

public interface FishingRepository extends JpaRepository<Fishing, Long> {
	boolean existsBySpotIdAndForecastDateAndTimePeriod(Long spotId, LocalDate forecastDate, String timePeriod);

	@Query(value = """
					SELECT DISTINCT f.spotId FROM Fishing f
					WHERE f.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	List<Fishing> findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	Optional<Fishing> findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		Long spotId,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	);

	Optional<Fishing> findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(LocalDateTime start, LocalDateTime end);

}
