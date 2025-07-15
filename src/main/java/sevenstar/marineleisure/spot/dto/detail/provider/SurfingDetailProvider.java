package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.dto.detail.items.SurfingSpotDetail;

@Component
@RequiredArgsConstructor
public class SurfingDetailProvider implements ActivityDetailProvider {
	private final SurfingRepository surfingRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.SURFING;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		return transform(surfingRepository.findForecasts(spotId, date));
	}

	private List<ActivitySpotDetail> transform(List<Surfing> surfingForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (Surfing surfingForecast : surfingForecasts) {
			details.add(SurfingSpotDetail.of(surfingForecast));
		}
		return details;
	}

}
