package sevenstar.marineleisure.spot.dto.detail;

import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivitySpotDetail;

public record SpotDetailReadResponse<T extends ActivitySpotDetail>(
	Long spotId,
	String name,
	ActivityCategory category,
	float latitude,
	float longitude,
	boolean isFavorite,
	List<T> detail
) {
}
