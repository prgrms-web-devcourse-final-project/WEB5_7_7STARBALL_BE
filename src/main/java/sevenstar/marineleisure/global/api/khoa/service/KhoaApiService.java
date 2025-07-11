package sevenstar.marineleisure.global.api.khoa.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.forecast.domain.FishingTarget;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.FishingTargetRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.api.khoa.KhoaApiClient;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.FishingItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.KhoaItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.MudflatItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.ScubaItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.SurfingItem;
import sevenstar.marineleisure.global.api.khoa.mapper.KhoaMapper;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.global.utils.GeoUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class KhoaApiService {
	private final KhoaApiClient khoaApiClient;
	private final OutdoorSpotRepository outdoorSpotRepository;
	private final FishingRepository fishingRepository;
	private final FishingTargetRepository fishingTargetRepository;
	private final MudflatRepository mudflatRepository;
	private final ScubaRepository scubaRepository;
	private final SurfingRepository surfingRepository;
	private final GeoUtils geoUtils;

	/**
	 * KHOA API를 통해 스쿠버, 낚시, 갯벌, 서핑 정보를 업데이트합니다.
	 * <p>
	 * 3일치 데이터를 가져오며, 각 카테고리별로 데이터를 저장합니다.
	 */
	// TODO : 리팩토링 필요
	@Transactional
	public void updateApi(LocalDate startDate, LocalDate endDate) {
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			String reqDate = DateUtils.parseDate(date);
			// scuba
			List<ScubaItem> scubaItems = getKhoaApiData(new ParameterizedTypeReference<>() {
			}, reqDate, ActivityCategory.SCUBA);

			for (ScubaItem item : scubaItems) {
				OutdoorSpot outdoorSpot = createOutdoorSpot(item, FishingType.NONE);
				scubaRepository.upsertScuba(outdoorSpot.getId(), DateUtils.parseDate(item.getPredcYmd()),
					TimePeriod.from(item.getPredcNoonSeCd()).name(), TidePhase.parse(item.getTdlvHrCn()).name(),
					TotalIndex.fromDescription(item.getTotalIndex()).name(), Float.parseFloat(item.getMinWvhgt()),
					Float.parseFloat(item.getMaxWvhgt()), Float.parseFloat(item.getMinWtem()),
					Float.parseFloat(item.getMaxWtem()), Float.parseFloat(item.getMinCrsp()),
					Float.parseFloat(item.getMaxCrsp()));
			}

			// fishing
			for (FishingType fishingType : FishingType.getFishingTypes()) {
				List<FishingItem> fishingItems = getKhoaApiData(new ParameterizedTypeReference<>() {
				}, reqDate, fishingType.getDescription());
				for (FishingItem item : fishingItems) {
					OutdoorSpot outdoorSpot = createOutdoorSpot(item, fishingType);
					Long targetId = item.getSeafsTgfshNm() == null ? null :
						fishingTargetRepository.findByName(item.getSeafsTgfshNm())
							.orElseGet(() -> fishingTargetRepository.save(KhoaMapper.toEntity(item.getSeafsTgfshNm())))
							.getId();
					fishingRepository.upsertFishing(outdoorSpot.getId(), targetId,
						DateUtils.parseDate(item.getPredcYmd()), TimePeriod.from(item.getPredcNoonSeCd()).name(),
						TidePhase.parse(item.getTdlvHrScr()).name(),
						TotalIndex.fromDescription(item.getTotalIndex()).name(), item.getMinWvhgt(), item.getMaxWvhgt(),
						item.getMinWtem(), item.getMaxWtem(), item.getMinArtmp(), item.getMinArtmp(), item.getMinCrsp(),
						item.getMaxCrsp(), item.getMinWspd(), item.getMaxWspd());
				}
			}

			// surfing
			List<SurfingItem> surfingItems = getKhoaApiData(new ParameterizedTypeReference<>() {
			}, reqDate, ActivityCategory.SURFING);

			for (SurfingItem item : surfingItems) {
				OutdoorSpot outdoorSpot = createOutdoorSpot(item, FishingType.NONE);

				surfingRepository.upsertSurfing(outdoorSpot.getId(), DateUtils.parseDate(item.getPredcYmd()),
					TimePeriod.from(item.getPredcNoonSeCd()).name(), Float.parseFloat(item.getAvgWvhgt()),
					Float.parseFloat(item.getAvgWvpd()), Float.parseFloat(item.getAvgWspd()),
					Float.parseFloat(item.getAvgWtem()), TotalIndex.fromDescription(item.getTotalIndex()).name());
			}

			// mudflat
			List<MudflatItem> mudflatItems = getKhoaApiData(new ParameterizedTypeReference<>() {
			}, reqDate, ActivityCategory.MUDFLAT);

			for (MudflatItem item : mudflatItems) {
				OutdoorSpot outdoorSpot = createOutdoorSpot(item, FishingType.NONE);

				mudflatRepository.upsertMudflat(outdoorSpot.getId(), DateUtils.parseDate(item.getPredcYmd()),
					LocalTime.parse(item.getMdftExprnBgngTm()), LocalTime.parse(item.getMdftExprnEndTm()),
					Float.parseFloat(item.getMinArtmp()), Float.parseFloat(item.getMaxArtmp()),
					Float.parseFloat(item.getMinWspd()), Float.parseFloat(item.getMaxWspd()), item.getWeather(),
					TotalIndex.fromDescription(item.getTotalIndex()).name());
			}
		}
	}

	@Transactional
	public OutdoorSpot createOutdoorSpot(KhoaItem item, FishingType fishingType) {
		return outdoorSpotRepository.findByLatitudeAndLongitudeAndCategory(item.getLatitude(), item.getLongitude(),
				item.getCategory())
			.orElseGet(() -> outdoorSpotRepository.save(
				KhoaMapper.toEntity(item, fishingType, geoUtils.createPoint(item.getLatitude(), item.getLongitude()))));
	}

	private <T> List<T> getKhoaApiData(ParameterizedTypeReference<ApiResponse<T>> responseType, String reqDate,
		ActivityCategory category) {
		List<T> result = new ArrayList<>();

		int page = 1;
		int size = 300;
		while (true) {
			ResponseEntity<ApiResponse<T>> response = khoaApiClient.get(responseType, reqDate, page++, size, category);
			result.addAll(response.getBody().getResponse().getBody().getItems().getItem());
			if (response.getBody().getResponse().getBody().getPageNo() * response.getBody()
				.getResponse()
				.getBody()
				.getNumOfRows() > response.getBody().getResponse().getBody().getTotalCount()) {
				break;
			}
		}
		return result;
	}

	private List<FishingItem> getKhoaApiData(ParameterizedTypeReference<ApiResponse<FishingItem>> responseType,
		String reqDate, String gubun) {
		List<FishingItem> result = new ArrayList<>();

		int page = 1;
		int size = 300;
		while (true) {
			ResponseEntity<ApiResponse<FishingItem>> response = khoaApiClient.get(responseType, reqDate, page++, size,
				gubun);
			result.addAll(response.getBody().getResponse().getBody().getItems().getItem());
			if (response.getBody().getResponse().getBody().getPageNo() * response.getBody()
				.getResponse()
				.getBody()
				.getNumOfRows() > response.getBody().getResponse().getBody().getTotalCount()) {
				break;
			}
		}
		return result;
	}
}

