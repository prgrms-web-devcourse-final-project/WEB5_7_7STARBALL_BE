package sevenstar.marineleisure.forecast.adapter.external;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import sevenstar.marineleisure.MarineLeisureApplication;

@SpringBootTest(classes = MarineLeisureApplication.class)
@ActiveProfiles("local")
class BadanuriApiClientTest {
	@Autowired
	private BadanuriApiClient badanuriApiClient;

	@Test
	void testCallApi() {
		String response = badanuriApiClient.callApi();
		System.out.println("✅ 바다누리 API 응답:\n" + response);
	}
}