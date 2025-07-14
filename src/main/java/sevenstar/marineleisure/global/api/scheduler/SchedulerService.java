package sevenstar.marineleisure.global.api.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.api.khoa.service.KhoaApiService;
import sevenstar.marineleisure.global.api.openmeteo.dto.service.OpenMeteoService;
import sevenstar.marineleisure.spot.repository.SpotViewQuartileRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SchedulerService {
	public static final int MAX_UPDATE_DAY = 3;
	private final KhoaApiService khoaApiService;
	private final OpenMeteoService openMeteoService;
	private final SpotViewQuartileRepository spotViewQuartileRepository;

	/**
	 * 앞으로의 스케줄링 전략에 의해 수정될 부분입니다.
	 * @author guwnoong
	 */
	// @Scheduled(initialDelay = 0, fixedDelay = 86400000)
	@Transactional
	public void scheduler() {
		LocalDate today = LocalDate.now();
		LocalDate endDate = today.plusDays(MAX_UPDATE_DAY);
		khoaApiService.updateApi(today, endDate);
		openMeteoService.updateApi(today, endDate);
		spotViewQuartileRepository.upsertQuartile();
		log.info("=== update data ===");
	}
}
