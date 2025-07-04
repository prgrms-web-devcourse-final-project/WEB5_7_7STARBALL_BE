package sevenstar.marineleisure.forecast.adapter.external;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest()
@ActiveProfiles("local")
class OpenMeteoApiClientTest {


	@Autowired
	private OpenMeteoApiClient openMeteoApiClient;

	@Test
	void testcallApi() {
		String response = openMeteoApiClient.callApi(37.57, 126.98);
		System.out.println("✅ Open-Meteo API 응답:\n" + response);
	}
}