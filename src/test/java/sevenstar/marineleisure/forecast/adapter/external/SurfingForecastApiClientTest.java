package sevenstar.marineleisure.forecast.adapter.external;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class SurfingForecastApiClientTest {
	@Autowired
	private SurfingForecastApiClient surfingForecastApiClient;

	@Test
	public void testCallApi() {
		String result = surfingForecastApiClient.callApi();
		System.out.println("✅ 서핑 API 응답 결과:");
		System.out.println(result);
	}
}