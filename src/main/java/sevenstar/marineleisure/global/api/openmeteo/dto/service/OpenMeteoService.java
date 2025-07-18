package sevenstar.marineleisure.global.api.openmeteo.dto.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivityProvider;

@Service
@RequiredArgsConstructor
public class OpenMeteoService {
	private final List<ActivityProvider> providers;

	@Transactional
	public void updateApi(LocalDate startDate, LocalDate endDate) {
		for (ActivityProvider provider : providers) {
			provider.update(startDate, endDate);
		}
	}

}
