package sevenstar.marineleisure.global.api.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.khoa.service.KhoaApiService;
import sevenstar.marineleisure.global.api.openmeteo.dto.service.OpenMeteoService;
import sevenstar.marineleisure.spot.repository.SpotViewQuartileRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchedulerService {
	public static final int MAX_READ_DAY = 3;
	public static final int MAX_UPDATE_DAY = 7;
	private final KhoaApiService khoaApiService;
	private final OpenMeteoService openMeteoService;
	private final SpotViewQuartileRepository spotViewQuartileRepository;

	/**
	 * 앞으로의 스케줄링 전략에 의해 수정될 부분입니다.
	 * @author guwnoong
	 */
	@Scheduled(initialDelay = 0, fixedDelay = 86400000)
	@Transactional
	public void scheduler() {
		LocalDate today = LocalDate.now();

		khoaApiService.updateApi(today);
		openMeteoService.updateApi(today, today.plusDays(MAX_UPDATE_DAY));
		spotViewQuartileRepository.upsertQuartile();
	}
}
