package sevenstar.marineleisure.forecast.repository;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.forecast.domain.Mudflat;

public interface MudflatRepository extends JpaRepository<Mudflat, Long> {
	boolean existsBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	@Query(value = """
					SELECT DISTINCT m.spotId FROM Mudflat m
					WHERE m.forecastDate BETWEEN :forecastDateAfter AND :forecastDateBefore
		""")
	List<Long> findByForecastDateBetween(@Param("forecastDateAfter") LocalDate forecastDateAfter,
		@Param("forecastDateBefore") LocalDate forecastDateBefore);

	Optional<Mudflat> findBySpotIdAndForecastDate(Long spotId, LocalDate forecastDate);

	Optional<Mudflat> findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		Long spotId,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime
	);

	Optional<Mudflat> findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(LocalDateTime start, LocalDateTime end);

	List<Mudflat> findBySpotId(Long spotId);
}