package sevenstar.marineleisure.global.api;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import sevenstar.marineleisure.global.api.khoa.service.KhoaApiService;
import sevenstar.marineleisure.global.api.openmeteo.dto.service.OpenMeteoService;
import sevenstar.marineleisure.global.api.scheduler.SchedulerService;

/**
 * 해당 테스트는 실제 API를 호출하여 데이터를 가져오는 통합 테스트입니다.
 * 수동으로 확인해보기 위함을 참고 부탁드립니다.
 * @author gunwoong
 */
@SpringBootTest
@Disabled
public class ApiServiceIntegrationTest {
	@Autowired
	private SchedulerService schedulerService;
	@Autowired
	private KhoaApiService khoaApiService;
	@Autowired
	private OpenMeteoService openMeteoService;

	@Test
	@Rollback(value = false)
	void should_activate() {
		schedulerService.scheduler();
	}

	@Test
	@Rollback(value = false)
	void should_testKhoaApiService() {
		int days = 3;
		LocalDate today = LocalDate.now();
		khoaApiService.updateApi(today);
	}

	@Test
	@Rollback(false)
	void should_testOpenMeteoService() {
		int days = 3;
		LocalDate today = LocalDate.now();
		openMeteoService.updateApi(today, today.plusDays(days));
	}
}
