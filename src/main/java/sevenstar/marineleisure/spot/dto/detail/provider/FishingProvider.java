package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.FishingTargetRepository;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.FishingItem;
import sevenstar.marineleisure.global.api.khoa.mapper.KhoaMapper;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.UvIndexItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.dto.EmailContent;
import sevenstar.marineleisure.spot.dto.projection.FishingReadProjection;
import sevenstar.marineleisure.spot.mapper.SpotDetailMapper;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class FishingProvider extends ActivityProvider {
	private final FishingRepository fishingRepository;
	private final FishingTargetRepository fishingTargetRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.FISHING;
	}

	@Override
	public ActivityRepository getSupportRepository() {
		return fishingRepository;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		List<FishingReadProjection> fishingForecasts = fishingRepository.findForecastsWithFish(spotId, date);
		return transform(fishingForecasts);
	}

	@Override
	public void upsert(LocalDate startDate, LocalDate endDate) {
		Map<FishingType, List<FishingItem>> data = new EnumMap<>(FishingType.class);

		for (FishingType fishingType : FishingType.getFishingTypes()) {
			data.put(fishingType, new ArrayList<>());
			for (LocalDate d = startDate; d.isBefore(endDate); d = d.plusDays(1)) {
				initApiData(new ParameterizedTypeReference<ApiResponse<FishingItem>>() {
				}, data.get(fishingType), d, d.plusDays(1), fishingType);
			}
		}

		for (Map.Entry<FishingType, List<FishingItem>> entry : data.entrySet()) {
			FishingType fishingType = entry.getKey();
			List<FishingItem> items = entry.getValue();
			for (FishingItem item : items) {
				OutdoorSpot outdoorSpot = createOutdoorSpot(item, fishingType);
				Long targetId = item.getSeafsTgfshNm() == null ? null :
					fishingTargetRepository.findByName(item.getSeafsTgfshNm())
						.orElseGet(() -> fishingTargetRepository.save(KhoaMapper.toEntity(item.getSeafsTgfshNm())))
						.getId();
				fishingRepository.upsertFishing(outdoorSpot.getId(), targetId, DateUtils.parseDate(item.getPredcYmd()),
					TimePeriod.from(item.getPredcNoonSeCd()).name(), TidePhase.parse(item.getTdlvHrScr()).name(),
					TotalIndex.fromDescription(item.getTotalIndex()).name(), item.getMinWvhgt(), item.getMaxWvhgt(),
					item.getMinWtem(), item.getMaxWtem(), item.getMinArtmp(), item.getMinArtmp(), item.getMinCrsp(),
					item.getMaxCrsp(), item.getMinWspd(), item.getMaxWspd());
			}

		}
	}

	@Override
	public void update(LocalDate startDate, LocalDate endDate) {
		for (Long spotId : fishingRepository.findByForecastDateBetween(startDate, endDate)) {
			OutdoorSpot outdoorSpot = outdoorSpotRepository.findById(spotId).orElseThrow();
			UvIndexItem uvIndex = getUvIndex(startDate, endDate, outdoorSpot.getLatitude().doubleValue(),
				outdoorSpot.getLongitude().doubleValue());
			for (int i = 0; i < uvIndex.getTime().size(); i++) {
				Float uvIndexValue = uvIndex.getUvIndexMax().get(i);
				LocalDate date = uvIndex.getTime().get(i);
				fishingRepository.updateUvIndex(uvIndexValue, spotId, date);
			}
		}
	}

	@Override
	public List<EmailContent> findEmailContent(TotalIndex totalIndex, LocalDate forecastDate) {
		return fishingRepository.findEmailContentByTotalIndexAndForecastDate(totalIndex, forecastDate);
	}

	private List<ActivitySpotDetail> transform(List<FishingReadProjection> fishingForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (FishingReadProjection fishingForecast : fishingForecasts) {
			details.add(SpotDetailMapper.toDto(fishingForecast));
		}
		return details;
	}

}
