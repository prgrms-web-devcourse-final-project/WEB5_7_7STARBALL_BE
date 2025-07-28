package sevenstar.marineleisure.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.global.enums.Region;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.domain.SpotPreset;

public interface SpotPresetRepository extends JpaRepository<SpotPreset, Region> {
	@Modifying
	@Query(value = """
		    INSERT INTO spot_preset (
		        region,
		        fishing_spot_id, fishing_name, fishing_total_index, fishing_month_view, fishing_week_view,
		        mudflat_spot_id, mudflat_name, mudflat_total_index, mudflat_month_view, mudflat_week_view,
		        scuba_spot_id, scuba_name, scuba_total_index, scuba_month_view, scuba_week_view,
		        surfing_spot_id, surfing_name, surfing_total_index, surfing_month_view, surfing_week_view
		    )
		    VALUES (
		        :region,
		        :fishingId, :fishingName, :fishingTotalIndex,:fishingMonthView, :fishingWeekView,
		        :mudflatId, :mudflatName, :mudflatTotalIndex,:mudflatMonthView, :mudflatWeekView,
		        :scubaId, :scubaName, :scubaTotalIndex,:scubaMonthView, :scubaWeekView,
		        :surfingId, :surfingName, :surfingTotalIndex,:surfingMonthView, :surfingWeekView
		    )
		    ON DUPLICATE KEY UPDATE
		        fishing_spot_id = VALUES(fishing_spot_id),
		        fishing_name = VALUES(fishing_name),
		        fishing_total_index = VALUES(fishing_total_index),
				fishing_month_view= VALUES(fishing_month_view),
				fishing_week_view=VALUES(fishing_week_view),
		        mudflat_spot_id = VALUES(mudflat_spot_id),
		        mudflat_name = VALUES(mudflat_name),
		        mudflat_total_index = VALUES(mudflat_total_index),
				mudflat_month_view = VALUES(mudflat_month_view),
				mudflat_week_view = VALUES(mudflat_week_view),
		        scuba_spot_id = VALUES(scuba_spot_id),
		        scuba_name = VALUES(scuba_name),
		        scuba_total_index = VALUES(scuba_total_index),
				scuba_month_view = VALUES(scuba_month_view),
				scuba_week_view = VALUES(scuba_week_view),
		        surfing_spot_id = VALUES(surfing_spot_id),
		        surfing_name = VALUES(surfing_name),
		        surfing_total_index = VALUES(surfing_total_index),
				surfing_month_view = VALUES(surfing_month_view),
				surfing_week_view = VALUES(surfing_week_view)
		""", nativeQuery = true)
	void upsert(
		@Param("region") String region,

		@Param("fishingId") Long fishingId,
		@Param("fishingName") String fishingName,
		@Param("fishingTotalIndex") String fishingTotalIndex,
		@Param("fishingMonthView") Integer fishingMonthView,
		@Param("fishingWeekView") Integer fishingWeekView,

		@Param("mudflatId") Long mudflatId,
		@Param("mudflatName") String mudflatName,
		@Param("mudflatTotalIndex") String mudflatTotalIndex,
		@Param("mudflatMonthView") Integer mudflatMonthView,
		@Param("mudflatWeekView") Integer mudflatWeekView,

		@Param("scubaId") Long scubaId,
		@Param("scubaName") String scubaName,
		@Param("scubaTotalIndex") String scubaTotalIndex,
		@Param("scubaMonthView") Integer scubaMonthView,
		@Param("scubaWeekView") Integer scubaWeekView,

		@Param("surfingId") Long surfingId,
		@Param("surfingName") String surfingName,
		@Param("surfingTotalIndex") String surfingTotalIndex,
		@Param("surfingMonthView") Integer surfingMonthView,
		@Param("surfingWeekView") Integer surfingWeekView
	);

}