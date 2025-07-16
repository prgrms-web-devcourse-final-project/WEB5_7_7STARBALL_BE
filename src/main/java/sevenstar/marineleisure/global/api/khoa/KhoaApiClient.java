package sevenstar.marineleisure.global.api.khoa;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.config.properties.KhoaProperties;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.FishingItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.global.utils.UriBuilder;

@Component
@RequiredArgsConstructor
public class KhoaApiClient {
	private final RestTemplate apiRestTemplate;
	private final KhoaProperties khoaProperties;

	/**
	 * khoa api get 요청(갯벌체험, 서핑, 스쿠버다이빙)
	 * @param responseType response 타입
	 * @param reqDate 요청 일자
	 * @param page
	 * @param size
	 * @param category 활동 카테고리
	 * @return response
	 * @param <T>
	 */
	public <T> ResponseEntity<T> get(ParameterizedTypeReference<T> responseType, LocalDate reqDate, int page, int size,
		ActivityCategory category) {
		if (category == ActivityCategory.FISHING) {
			// TODO : handling exception
			throw new CustomException(CommonErrorCode.INTERNET_SERVER_ERROR);
		}
		URI uri = UriBuilder.buildQueryParameter(khoaProperties.getBaseUrl(), khoaProperties.getPath(category),
			khoaProperties.getParams(DateUtils.formatTime(reqDate), page, size));
		return apiRestTemplate.exchange(uri, HttpMethod.GET, null, responseType);
	}

	/**
	 * khoa api get 요청(낚시)
	 * @param responseType response 타입
	 * @param reqDate 요청 일자
	 * @param page
	 * @param size
	 * @param gubun 선상 / 갯바위 중 하나
	 * @return response
	 */
	public ResponseEntity<ApiResponse<FishingItem>> get(
		ParameterizedTypeReference<ApiResponse<FishingItem>> responseType, LocalDate reqDate, int page, int size,
		FishingType gubun) {
		URI uri = UriBuilder.buildQueryParameter(khoaProperties.getBaseUrl(),
			khoaProperties.getPath(ActivityCategory.FISHING), khoaProperties.getParams(DateUtils.formatTime(reqDate), page, size, gubun.getDescription()));
		return apiRestTemplate.exchange(uri, HttpMethod.GET, null, responseType);
	}

}
