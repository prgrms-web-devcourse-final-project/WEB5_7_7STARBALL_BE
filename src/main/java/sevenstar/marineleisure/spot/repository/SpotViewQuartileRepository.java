package sevenstar.marineleisure.spot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import sevenstar.marineleisure.spot.domain.SpotViewQuartile;

public interface SpotViewQuartileRepository extends JpaRepository<SpotViewQuartile, Long> {
	Optional<SpotViewQuartile> findBySpotId(Long spotId);

	@Modifying
	@Transactional
	@Query(value = """
		INSERT INTO spot_view_quartile (spot_id, month_quartile, week_quartile, updated_at)
		SELECT
		    spot_id,
		    NTILE(4) OVER (ORDER BY SUM(view_count)) AS month_quartile,
		    NTILE(4) OVER (
		        ORDER BY SUM(CASE WHEN view_date >= CURDATE() - INTERVAL 7 DAY THEN view_count ELSE 0 END)
		    ) AS week_quartile,
		    CURDATE()
		FROM spot_view_stats
		WHERE view_date >= CURDATE() - INTERVAL 30 DAY
		GROUP BY spot_id
		ON DUPLICATE KEY UPDATE
		    month_quartile = VALUES(month_quartile),
		    week_quartile = VALUES(week_quartile),
		    updated_at = CURDATE()
		""", nativeQuery = true)
	void upsertQuartile();
}