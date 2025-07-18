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
        fishing_spot_id, fishing_name, fishing_total_index,
        mudflat_spot_id, mudflat_name, mudflat_total_index,
        scuba_spot_id, scuba_name, scuba_total_index,
        surfing_spot_id, surfing_name, surfing_total_index
    )
    VALUES (
        :region,
        :fishingId, :fishingName, :fishingTotalIndex,
        :mudflatId, :mudflatName, :mudflatTotalIndex,
        :scubaId, :scubaName, :scubaTotalIndex,
        :surfingId, :surfingName, :surfingTotalIndex
    )
    ON DUPLICATE KEY UPDATE
        fishing_spot_id = VALUES(fishing_spot_id),
        fishing_name = VALUES(fishing_name),
        fishing_total_index = VALUES(fishing_total_index),
        mudflat_spot_id = VALUES(mudflat_spot_id),
        mudflat_name = VALUES(mudflat_name),
        mudflat_total_index = VALUES(mudflat_total_index),
        scuba_spot_id = VALUES(scuba_spot_id),
        scuba_name = VALUES(scuba_name),
        scuba_total_index = VALUES(scuba_total_index),
        surfing_spot_id = VALUES(surfing_spot_id),
        surfing_name = VALUES(surfing_name),
        surfing_total_index = VALUES(surfing_total_index)
""", nativeQuery = true)
	void upsert(
		@Param("region") String region,

		@Param("fishingId") Long fishingId,
		@Param("fishingName") String fishingName,
		@Param("fishingTotalIndex") String fishingTotalIndex,

		@Param("mudflatId") Long mudflatId,
		@Param("mudflatName") String mudflatName,
		@Param("mudflatTotalIndex") String mudflatTotalIndex,

		@Param("scubaId") Long scubaId,
		@Param("scubaName") String scubaName,
		@Param("scubaTotalIndex") String scubaTotalIndex,

		@Param("surfingId") Long surfingId,
		@Param("surfingName") String surfingName,
		@Param("surfingTotalIndex") String surfingTotalIndex
	);

}