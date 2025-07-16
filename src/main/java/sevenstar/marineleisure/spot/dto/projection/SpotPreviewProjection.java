package sevenstar.marineleisure.spot.dto.projection;

import sevenstar.marineleisure.global.enums.TotalIndex;

public interface SpotPreviewProjection {
	Long getSpotId();
	String getName();
	TotalIndex getTotalIndex();
}
