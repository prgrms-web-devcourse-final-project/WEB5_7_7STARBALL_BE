package sevenstar.marineleisure.spot.dto;

import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;

public record SpotReadResponse(
	List<SpotInfo> spots
) {
	public record SpotInfo(
		Long id,
		String name,
		ActivityCategory category,
		Float latitude,
		Float longitude,
		Float distance,
		String currentStatus,
		Integer monthView,
		Integer weekView,
		boolean isFavorite
	) {

	}
}
