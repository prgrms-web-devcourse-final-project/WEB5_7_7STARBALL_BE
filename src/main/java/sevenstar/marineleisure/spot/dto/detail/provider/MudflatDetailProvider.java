package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.mapper.SpotDetailMapper;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class MudflatDetailProvider implements ActivityDetailProvider {
	private final MudflatRepository mudflatRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.MUDFLAT;
	}

	@Override
	public ActivityRepository getSupportRepository() {
		return mudflatRepository;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		return transform(mudflatRepository.findForecasts(spotId, date));
	}

	private List<ActivitySpotDetail> transform(List<Mudflat> mudflatForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (Mudflat mudflatForecast : mudflatForecasts) {
			details.add(SpotDetailMapper.toDto(mudflatForecast));
		}
		return details;
	}
}
