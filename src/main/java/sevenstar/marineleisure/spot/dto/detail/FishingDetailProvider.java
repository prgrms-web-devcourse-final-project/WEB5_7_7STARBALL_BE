package sevenstar.marineleisure.spot.dto.detail;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.mapper.SpotMapper;

@Component
@RequiredArgsConstructor
public class FishingDetailProvider implements ActivityDetailProvider {
	private final FishingRepository fishingRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.FISHING;
	}

	@Override
	public List<ActivityDetailResponse> getDetails(Long spotId, LocalDate date) {
		List<FishingReadResponse> fishingForecasts = fishingRepository.findFishingForecasts(
			spotId, date);
		result.addAll(SpotMapper.toFishingSpotDetails(fishingForecasts));
		return List.of();
	}
}
