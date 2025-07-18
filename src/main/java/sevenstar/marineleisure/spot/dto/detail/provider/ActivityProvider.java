package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import sevenstar.marineleisure.global.api.khoa.KhoaApiClient;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.KhoaItem;
import sevenstar.marineleisure.global.api.khoa.mapper.KhoaMapper;
import sevenstar.marineleisure.global.api.openmeteo.OpenMeteoApiClient;
import sevenstar.marineleisure.global.api.openmeteo.dto.common.OpenMeteoReadResponse;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.SunTimeItem;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.UvIndexItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.utils.GeoUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.ActivityRepository;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

public abstract class ActivityProvider {
	@Autowired
	protected OutdoorSpotRepository outdoorSpotRepository;
	@Autowired
	private GeoUtils geoUtils;
	@Autowired
	private KhoaApiClient khoaApiClient;
	@Autowired
	private OpenMeteoApiClient openMeteoApiClient;

	abstract ActivityCategory getSupportCategory();

	public abstract ActivityRepository getSupportRepository();

	public abstract List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date);

	public abstract void upsert(LocalDate startDate, LocalDate endDate);

	public abstract void update(LocalDate startDate, LocalDate endDate);

	@Transactional
	protected OutdoorSpot createOutdoorSpot(KhoaItem item, FishingType fishingType) {
		return outdoorSpotRepository.findByLatitudeAndLongitudeAndCategory(item.getLatitude(), item.getLongitude(),
				item.getCategory())
			.orElseGet(() -> outdoorSpotRepository.save(
				KhoaMapper.toEntity(item, fishingType, geoUtils.createPoint(item.getLatitude(), item.getLongitude()))));
	}

	protected <T extends KhoaItem> void initApiData(ParameterizedTypeReference<ApiResponse<T>> responseType,
		List<T> items, LocalDate date, LocalDate endDate, FishingType fishingType) {
		int page = 1;
		int size = 300;
		while (true) {
			ResponseEntity<ApiResponse<T>> response = khoaApiClient.get(responseType, date, page++, size,
				getSupportCategory(), fishingType);
			for (T item : response.getBody().getResponse().getBody().getItems().getItem()) {
				if (!item.getForecastDate().isBefore(endDate)) {
					continue;
				}
				items.add(item);
			}
			if (response.getBody().getResponse().getBody().getPageNo() * response.getBody()
				.getResponse()
				.getBody()
				.getNumOfRows() > response.getBody().getResponse().getBody().getTotalCount()) {
				break;
			}
		}
	}

	protected SunTimeItem getSunTimes(LocalDate startDate, LocalDate endDate, double latitude, double longitude) {
		ResponseEntity<OpenMeteoReadResponse<SunTimeItem>> response = openMeteoApiClient.getSunTimes(
			new ParameterizedTypeReference<>() {
			}, startDate, endDate, latitude, longitude);
		return response.getBody().getDaily();
	}

	protected UvIndexItem getUvIndex(LocalDate startDate, LocalDate endDate, double latitude, double longitude) {
		ResponseEntity<OpenMeteoReadResponse<UvIndexItem>> response = openMeteoApiClient.getUvIndex(
			new ParameterizedTypeReference<>() {
			}, startDate, endDate, latitude, longitude);
		return response.getBody().getDaily();
	}

}
