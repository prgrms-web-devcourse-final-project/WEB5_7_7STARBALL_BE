package sevenstar.marineleisure.spot.dto;

import java.util.List;

public record SpotReadResponse(
	List<SpotInfo> spots
) {
	public record SpotInfo(
		Long id,
		String name,
		Float latitude,
		Float longitude,
		Float distance,
		String currentStatus,
		String crowdLevel,
		boolean isFavorite
	) {

	}
}
