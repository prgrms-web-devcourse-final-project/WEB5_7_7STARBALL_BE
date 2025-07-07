package sevenstar.marineleisure.global.api.khoa;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sevenstar.marineleisure.global.api.khoa.dto.DivingApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.FishingApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.MudflatApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.SurfingApiResponse;
import sevenstar.marineleisure.global.enums.ActivityCategory;

@SpringBootTest
class KhoaApiClientTest {
	@Autowired
	private KhoaApiClient khoaApiClient;

	private String reqDate = "2025070100";

	@Test
	void receiveFishApi() {
		ResponseEntity<FishingApiResponse> response = khoaApiClient.get(FishingApiResponse.class, reqDate, 1, 15,
			"갯바위");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void should_receiveFishApi_when_givenDate() {
		ResponseEntity<FishingApiResponse> response = khoaApiClient.get(FishingApiResponse.class, reqDate, 1, 300,
			"갯바위");
	}

	@Test
	void receiveSurfingApi() {
		ResponseEntity<SurfingApiResponse> response = khoaApiClient.get(SurfingApiResponse.class, reqDate, 1, 15,
			ActivityCategory.SURFING
		);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveMudflatApi() {
		ResponseEntity<MudflatApiResponse> response = khoaApiClient.get(MudflatApiResponse.class, reqDate, 1, 15,
			ActivityCategory.MUDFLAT
		);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveDivingApi() {
		ResponseEntity<DivingApiResponse> response = khoaApiClient.get(DivingApiResponse.class, reqDate, 1, 15,
			ActivityCategory.DIVING
		);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

}