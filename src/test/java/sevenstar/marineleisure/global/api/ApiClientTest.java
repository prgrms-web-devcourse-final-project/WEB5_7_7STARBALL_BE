package sevenstar.marineleisure.global.api;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sevenstar.marineleisure.global.api.kakao.KakaoApiClient;
import sevenstar.marineleisure.global.api.kakao.dto.RegionResponse;
import sevenstar.marineleisure.global.api.khoa.KhoaApiClient;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.FishingItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.MudflatItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.ScubaItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.SurfingItem;
import sevenstar.marineleisure.global.api.openmeteo.OpenMeteoApiClient;
import sevenstar.marineleisure.global.api.openmeteo.dto.common.OpenMeteoReadResponse;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.SunTimeItem;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.UvIndexItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;

/**
 * 외부 API 클라이언트 조회 테스트
 */
@SpringBootTest
@Disabled
public class ApiClientTest {
	@Autowired
	private KhoaApiClient khoaApiClient;
	@Autowired
	private OpenMeteoApiClient openMeteoApiClient;
	@Autowired
	private KakaoApiClient kakaoApiClient;

	private LocalDate reqDate = LocalDate.now();

	@Test
	void receiveFishApi() {
		ResponseEntity<ApiResponse<FishingItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, ActivityCategory.FISHING, FishingType.ROCK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveSurfingApi() {
		ResponseEntity<ApiResponse<SurfingItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, ActivityCategory.SURFING, FishingType.NONE);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveMudflatApi() {
		ResponseEntity<ApiResponse<MudflatItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, ActivityCategory.MUDFLAT, FishingType.NONE);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveDivingApi() {
		ResponseEntity<ApiResponse<ScubaItem>> response = khoaApiClient.get(new ParameterizedTypeReference<>() {
		}, reqDate, 1, 15, ActivityCategory.SCUBA, FishingType.NONE);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResponse().getBody().getItems().getItem()).hasSize(15);
	}

	@Test
	void receiveSunTimes() {
		ResponseEntity<OpenMeteoReadResponse<SunTimeItem>> result = openMeteoApiClient.getSunTimes(
			new ParameterizedTypeReference<>() {
			}, LocalDate.now(), LocalDate.now(), 37.526126, 126.922255
		);

		assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().getDaily().getTime().size()).isEqualTo(
			result.getBody().getDaily().getSunrise().size());
		assertThat(result.getBody().getDaily().getTime().size()).isEqualTo(
			result.getBody().getDaily().getSunset().size());
		assertThat(result.getBody().getDaily()).isNotNull();
	}

	@Test
	void receiveUvIndex() {
		ResponseEntity<OpenMeteoReadResponse<UvIndexItem>> result = openMeteoApiClient.getUvIndex(
			new ParameterizedTypeReference<>() {
			}, LocalDate.now(), LocalDate.now(), 37.526126, 126.922255
		);

		assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().getDaily().getTime().size()).isEqualTo(
			result.getBody().getDaily().getUvIndexMax().size());
		assertThat(result.getBody().getDaily()).isNotNull();
	}

	@Test
	void receiveRegion() {
		float latitude = 36.3777f;
		float longitude = 127.3727f;

		ResponseEntity<RegionResponse> regionResponse = kakaoApiClient.get(latitude, longitude);
		assertThat(regionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(regionResponse.getBody().getDocuments().getFirst().getAddress_name()).startsWith("대전광역시");
	}
}
