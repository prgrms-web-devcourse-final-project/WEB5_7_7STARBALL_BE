package sevenstar.marineleisure.spot.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.spot.repository.SpotViewQuartileRepository;

@Service
@RequiredArgsConstructor
public class SpotSchedulerService {
	private final SpotViewQuartileRepository spotViewQuartileRepository;

	@Scheduled(initialDelay = 0, fixedRate = 3600_000)
	@Transactional
	public void updateSpotViewQuartile() {
		spotViewQuartileRepository.upsertQuartile();
	}
}
