package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.mapper.SpotDetailMapper;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class ScubaDetailProvider implements ActivityDetailProvider {
	private final ScubaRepository scubaRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.SCUBA;
	}

	@Override
	public ActivityRepository getSupportRepository() {
		return scubaRepository;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		return transform(scubaRepository.findForecasts(spotId, date));
	}

	private List<ActivitySpotDetail> transform(List<Scuba> scubaForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (Scuba scubaForecast : scubaForecasts) {
			details.add(SpotDetailMapper.toDto(scubaForecast));
		}
		return details;
	}

}
