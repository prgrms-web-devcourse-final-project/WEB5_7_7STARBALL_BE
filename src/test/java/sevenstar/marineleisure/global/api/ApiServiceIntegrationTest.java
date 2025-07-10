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
 * 테스트를 실행하기 전에 외부 API의 상태와 응답을 확인해야 합니다.
 * 동작확인을 위해 아래와 같이 임시적으로 테스트를 작성했고 앞으로 테스트 방식은
 * 회의를 통해 논의하여 변경할 예정입니다.
 * @author gunwoong
 */
// @DataJpaTest
// @Import({SchedulerService.class, KhoaApiClient.class, OpenMeteoApiClient.class, RestTemplate.class})
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
		khoaApiService.updateApi(today, today.plusDays(days));
	}

	@Test
	@Rollback(false)
	void should_testOpenMeteoService() {
		int days = 3;
		LocalDate today = LocalDate.now();
		openMeteoService.updateApi(today, today.plusDays(days));
	}
}
