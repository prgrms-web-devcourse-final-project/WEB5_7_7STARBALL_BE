package sevenstar.marineleisure.forecast.adapter.external;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class ScubaForecastApiClientTest {

	@Autowired
	private ScubaForecastApiClient scubaForecastApiClient;

	@Test
	public void testCallApi() {
		String result = scubaForecastApiClient.callApi();
		System.out.println("✅ 스킨스쿠버 API 응답 결과:");
		System.out.println(result);
	}
}