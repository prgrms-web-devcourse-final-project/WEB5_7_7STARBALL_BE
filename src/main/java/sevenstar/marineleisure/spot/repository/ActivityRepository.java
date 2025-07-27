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

}
