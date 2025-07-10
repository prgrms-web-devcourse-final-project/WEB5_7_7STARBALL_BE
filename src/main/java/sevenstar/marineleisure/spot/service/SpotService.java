package sevenstar.marineleisure.spot.service;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.dto.SpotCreateRequest;
import sevenstar.marineleisure.spot.dto.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;

public interface SpotService {
	SpotReadResponse searchSpot(float latitude, float longitude, ActivityCategory category);

	SpotReadResponse searchAllSpot(float latitude, float longitude);

	<T> SpotDetailReadResponse<T> searchSpotDetail(Long spotId);

	void createOutdoorSpot(SpotCreateRequest spotCreateRequest);

	void upsertSpotViewStats(Long spotId);
}
