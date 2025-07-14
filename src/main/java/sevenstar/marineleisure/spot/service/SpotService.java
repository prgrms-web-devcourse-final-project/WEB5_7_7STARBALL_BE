package sevenstar.marineleisure.spot.service;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.dto.detail.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.SpotPreviewReadResponse;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;

public interface SpotService {
	SpotReadResponse searchSpot(float latitude, float longitude, Integer radius, ActivityCategory category);

	<T> SpotDetailReadResponse<T> searchSpotDetail(Long spotId);

	SpotPreviewReadResponse preview(float latitude, float longitude);

	void upsertSpotViewStats(Long spotId);
}
