package sevenstar.marineleisure.global.api.khoa.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.khoa.KhoaApiClient;
import sevenstar.marineleisure.global.api.khoa.dto.FishingApiResponse;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Service
@RequiredArgsConstructor
public class KhoaApiService {
	private final KhoaApiClient khoaApiClient;
	private final OutdoorSpotRepository outdoorSpotRepository;

	public void updateFishApi(String gubun, String reqDate) {
		int page = 1;
		int size = 300;
		while (true) {
			ResponseEntity<FishingApiResponse> response = khoaApiClient.get(FishingApiResponse.class, reqDate, page,
				size, gubun);
			if (!response.getStatusCode().is2xxSuccessful()) {
				// throw new IllegalAccessException()
			}
			// response.getBody().get
		}
		// for (FishingType fishingType : FishingType.values()) {
		// 	for (String reqDate : DateUtils.getRangeDateListFromNow(2)) {
		//
		// 	}
		// }

	}
}
