package sevenstar.marineleisure.spot.dto.projection;

import sevenstar.marineleisure.global.enums.TotalIndex;

public interface BestSpotProjection {
	Long getId();
	String getName();
	TotalIndex getTotalIndex();
}
