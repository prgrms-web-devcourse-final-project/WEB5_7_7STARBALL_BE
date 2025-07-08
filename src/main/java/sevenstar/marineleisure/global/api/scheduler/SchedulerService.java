package sevenstar.marineleisure.global.api.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.khoa.service.KhoaApiService;
import sevenstar.marineleisure.global.api.openmeteo.dto.service.OpenMeteoService;

@Service
@RequiredArgsConstructor
public class SchedulerService {
	private static final int MAX_EXPECT_DAY = 3;
	private final KhoaApiService khoaApiService;
	private final OpenMeteoService openMeteoService;

	/**
	 * 앞으로의 스케줄링 전략에 의해 수정될 부분입니다.
	 * 스케줄링 예제 ) 초 분 시 일 월 요일
	 * @author guwnoong
	 */
	@Scheduled(cron = "0 0 1 * * MON")
	public void scheduler() {
		LocalDate today = LocalDate.now();

		khoaApiService.updateApi(today, today.plusDays(MAX_EXPECT_DAY));
		openMeteoService.updateApi(today, today.plusDays(MAX_EXPECT_DAY));
	}
}
