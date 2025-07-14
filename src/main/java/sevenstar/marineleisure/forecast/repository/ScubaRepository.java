package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

	Optional<Scuba> findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		Long spotId,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	);

	Optional<Scuba> findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(LocalDateTime start, LocalDateTime end);

	Optional<Scuba> findBySpotIdAndCreatedAtBeforeOrderByCreatedAtDesc(Long spotId, LocalDateTime createdAtBefore);

	// Optional<Scuba> findBySpotIdOrderByCreatedAt(Long spotId);
}