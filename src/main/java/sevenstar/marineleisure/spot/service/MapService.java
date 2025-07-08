package sevenstar.marineleisure.spot.service;

import sevenstar.marineleisure.spot.dto.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;

public interface MapService {
	<T> SpotDetailReadResponse<T> searchSpotDetail(Long spotId);

	SpotReadResponse searchSpot(float latitude, float longitude, String category);

	void createOutdoorSpot(float latitude, float longitude, String location);
}
