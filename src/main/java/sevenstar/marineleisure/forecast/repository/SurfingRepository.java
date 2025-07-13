package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.forecast.domain.Surfing;

public interface SurfingRepository extends JpaRepository<Surfing, Long> {
	boolean existsBySpotIdAndForecastDateAndTimePeriod(Long spotId, LocalDate forecastDate, String timePeriod);

	@Query(value = """
					SELECT DISTINCT s.spotId FROM Surfing s
					WHERE s.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	List<Surfing> findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	Optional<Surfing> findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		Long spotId,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	);

	Optional<Surfing> findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(LocalDateTime start, LocalDateTime end);

}