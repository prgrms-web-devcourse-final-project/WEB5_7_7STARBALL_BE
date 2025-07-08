package sevenstar.marineleisure.global.api.khoa;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.FishingItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.MudflatItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.ScubaItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.SurfingItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;

/**
 * 해당 테스트는 외부 API 테스트입니다.
 * 실제 API 호출이 이뤄짐으로 , 운영 환경에서 테스트가 실행되지 않도록 @Disabled 어노테이션을 설정하였습니다.
 * 해당 테스트를 통해 Client 사용 예시를 참고해주시기 바랍니다.
 * @author gunwoong
 */
@SpringBootTest
@Disabled
class KhoaApiClientTest {
	@Autowired
	private KhoaApiClient khoaApiClient;

	private String reqDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	@Test
	void receiveFishApi() {
		ResponseEntity<ApiResponse<FishingItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, "갯바위");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveSurfingApi() {
		ResponseEntity<ApiResponse<SurfingItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, ActivityCategory.SURFING);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveMudflatApi() {
		ResponseEntity<ApiResponse<MudflatItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, ActivityCategory.MUDFLAT);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveDivingApi() {
		ResponseEntity<ApiResponse<ScubaItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, ActivityCategory.SCUBA);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}
}