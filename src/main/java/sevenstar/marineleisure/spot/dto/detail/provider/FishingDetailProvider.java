package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.dto.detail.items.FishingSpotDetail;
import sevenstar.marineleisure.spot.dto.projection.FishingReadProjection;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class FishingDetailProvider implements ActivityDetailProvider {
	private final FishingRepository fishingRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.FISHING;
	}

	@Override
	public ActivityRepository getSupportRepository() {
		return fishingRepository;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		List<FishingReadProjection> fishingForecasts = fishingRepository.findForecastsWithFish(spotId, date);
		return transform(fishingForecasts);
	}

	private List<ActivitySpotDetail> transform(List<FishingReadProjection> fishingForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (FishingReadProjection fishingForecast : fishingForecasts) {
			details.add(FishingSpotDetail.of(fishingForecast));
		}
		return details;
	}

}
