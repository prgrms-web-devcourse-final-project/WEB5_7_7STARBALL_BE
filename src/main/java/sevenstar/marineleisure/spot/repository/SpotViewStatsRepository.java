package sevenstar.marineleisure.spot.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.spot.domain.SpotViewStats;
import sevenstar.marineleisure.spot.domain.SpotViewStatsId;
import sevenstar.marineleisure.spot.dto.projection.SpotViewStatsProjection;

public interface SpotViewStatsRepository extends JpaRepository<SpotViewStats, SpotViewStatsId> {

	@Modifying
	@Query(value = """
		   INSERT INTO spot_view_stats (spot_id, view_date, view_count)
				   VALUES (:spotId,:viewDate,1) ON DUPLICATE KEY UPDATE view_count = view_count + 1
		""", nativeQuery = true)
	void upsertViewStats(@Param("spotId") Long spotId, @Param("viewDate") LocalDate viewDate);

	@Query(value = """
		SELECT
		    SUM(CASE WHEN view_date BETWEEN :targetDate - INTERVAL 6 DAY AND :targetDate THEN view_count ELSE 0 END) AS weekly_count,
		    SUM(CASE WHEN view_date BETWEEN :targetDate - INTERVAL 29 DAY AND :targetDate THEN view_count ELSE 0 END) AS monthly_count
		FROM spot_view_stats
		WHERE spot_id = :spotId
		  AND view_date BETWEEN :targetDate - INTERVAL 29 DAY AND :targetDate
		""", nativeQuery = true)
	SpotViewStatsProjection findSpotViewStatsBySpotId(@Param("spotId") Long spotId,
		@Param("targetDate") LocalDate targetDate);

}