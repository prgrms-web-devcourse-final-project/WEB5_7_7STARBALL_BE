package sevenstar.marineleisure.spot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.EmailContent;

@NoRepositoryBean
public interface ActivityRepository<T, ID> extends JpaRepository<T, ID> {
	@Query("""
		SELECT e.totalIndex
		FROM #{#entityName} e
		WHERE e.spotId = :spotId AND e.forecastDate = :date
		""")
	Slice<TotalIndex> findTotalIndex(@Param("spotId") Long spotId, @Param("date") LocalDate date, Pageable pageable);

	@Query("""
		    SELECT e
			FROM #{#entityName} e
		    WHERE e.spotId = :spotId
		    	AND e.forecastDate = :date
		""")
	List<T> findForecasts(@Param("spotId") Long spotId, @Param("date") LocalDate date);

	@Query(value = """
		SELECT new sevenstar.marineleisure.spot.dto.EmailContent(o.id,o.name,o.category)
		FROM OutdoorSpot o
		JOIN #{#entityName} e ON o.id=e.spotId
		WHERE e.totalIndex = :totalIndex
		  AND e.forecastDate = :forecastDate
		""")
	List<EmailContent> findEmailContentByTotalIndexAndForecastDate(TotalIndex totalIndex, LocalDate forecastDate);

}
