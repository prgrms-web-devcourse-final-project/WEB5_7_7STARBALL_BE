package sevenstar.marineleisure.spot.dto;

import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.projection.SpotPreviewProjection;

public record SpotPreviewReadResponse(
	SpotPreview fishing,
	SpotPreview mudflat,
	SpotPreview surfing,
	SpotPreview scuba
) {

	public record SpotPreview(
		Long spotId,
		String name,
		TotalIndex totalIndex
	) {
		public static SpotPreview from(SpotPreviewProjection spotPreviewProjection) {
			return new SpotPreview(spotPreviewProjection.getSpotId(), spotPreviewProjection.getName(),
				spotPreviewProjection.getTotalIndex());
		}
	}
}
