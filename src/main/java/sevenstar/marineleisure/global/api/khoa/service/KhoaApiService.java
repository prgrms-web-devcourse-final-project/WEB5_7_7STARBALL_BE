package sevenstar.marineleisure.global.api.khoa.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
import sevenstar.marineleisure.global.api.khoa.dto.item.MudflatItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.ScubaItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.SurfingItem;
import sevenstar.marineleisure.global.api.khoa.mapper.KhoaMapper;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class KhoaApiService {
	private static final int MAX_EXPECT_DAY = 7;
	private final KhoaApiClient khoaApiClient;
	private final OutdoorSpotRepository outdoorSpotRepository;
	private final FishingRepository fishingRepository;
	private final FishingTargetRepository fishingTargetRepository;
	private final MudflatRepository mudflatRepository;
	private final ScubaRepository scubaRepository;
	private final SurfingRepository surfingRepository;

	/**
	 * KHOA API를 통해 스쿠버, 낚시, 갯벌, 서핑 정보를 업데이트합니다.
	 * <p>
	 * 최대 7일치 데이터를 가져오며, 각 카테고리별로 데이터를 저장합니다.
	 */
	// TODO : 리팩토링 필요
	@Scheduled(cron = "0 0 1 * * MON") // 초 분 시 일 월 요일
	@Transactional
	public void updateApi() {
		FishingTarget emptyFishTarget = fishingTargetRepository.findByName("EMPTY").orElseGet(
			() -> fishingTargetRepository.save(KhoaMapper.toEntity("EMPTY"))
		);

		for (String reqDate : DateUtils.getRangeDateListFromNow(MAX_EXPECT_DAY)) {
			// scuba
			List<ScubaItem> scubaItems = getTotalApi(new ParameterizedTypeReference<>() {
			}, reqDate, ActivityCategory.SCUBA);

			for (ScubaItem item : scubaItems) {
				OutdoorSpot outdoorSpot = outdoorSpotRepository.findByLocation(item.getLocation()).orElseGet(
					() -> outdoorSpotRepository.save(KhoaMapper.toEntity(item, FishingType.NONE))
				);
				scubaRepository.save(KhoaMapper.toEntity(item, outdoorSpot.getId()));
			}

			// fishing
			for (FishingType fishingType : FishingType.getFishingTypes()) {
				List<FishingItem> fishingItems = getTotalApi(new ParameterizedTypeReference<>() {
				}, reqDate, fishingType.getDescription());
				for (FishingItem item : fishingItems) {
					OutdoorSpot outdoorSpot = outdoorSpotRepository.findByLocation(item.getLocation()).orElseGet(
						() -> outdoorSpotRepository.save(KhoaMapper.toEntity(item, fishingType))
					);
					if (item.getSeafsTgfshNm() == null) {
						fishingRepository.save(
							KhoaMapper.toEntity(item, outdoorSpot.getId(), emptyFishTarget.getId()));
						continue;
					}
					FishingTarget fishingTarget = fishingTargetRepository.findByName(
						item.getSeafsTgfshNm()).orElseGet(
						() -> fishingTargetRepository.save(KhoaMapper.toEntity(item.getSeafsTgfshNm()))
					);
					fishingRepository.save(
						KhoaMapper.toEntity(item, outdoorSpot.getId(), fishingTarget.getId()));

				}
			}

			// surfing
			List<SurfingItem> surfingItems = getTotalApi(new ParameterizedTypeReference<>() {
			}, reqDate, ActivityCategory.SURFING);

			for (SurfingItem item : surfingItems) {
				OutdoorSpot outdoorSpot = outdoorSpotRepository.findByLocation(item.getLocation()).orElseGet(
					() -> outdoorSpotRepository.save(KhoaMapper.toEntity(item, FishingType.NONE))
				);
				surfingRepository.save(KhoaMapper.toEntity(item, outdoorSpot.getId()));
			}

			// mudflat
			List<MudflatItem> mudflatItems = getTotalApi(new ParameterizedTypeReference<>() {
			}, reqDate, ActivityCategory.MUDFLAT);

			for (MudflatItem item : mudflatItems) {
				OutdoorSpot outdoorSpot = outdoorSpotRepository.findByLocation(item.getLocation()).orElseGet(
					() -> outdoorSpotRepository.save(KhoaMapper.toEntity(item, FishingType.NONE))
				);
				mudflatRepository.save(KhoaMapper.toEntity(item, outdoorSpot.getId()));
			}
		}
	}

	private <T> List<T> getTotalApi(ParameterizedTypeReference<ApiResponse<T>> responseType, String reqDate,
		ActivityCategory category) {
		List<T> result = new ArrayList<>();

		int page = 1;
		int size = 300;
		while (true) {
			ResponseEntity<ApiResponse<T>> response = khoaApiClient.get(responseType, reqDate, page++, size,
				category);
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

	private List<FishingItem> getTotalApi(ParameterizedTypeReference<ApiResponse<FishingItem>> responseType,
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
