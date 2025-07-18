package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.SurfingItem;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.UvIndexItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.mapper.SpotDetailMapper;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class SurfingProvider extends ActivityProvider {
	private final SurfingRepository surfingRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.SURFING;
	}

	@Override
	public ActivityRepository getSupportRepository() {
		return surfingRepository;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		return transform(surfingRepository.findForecasts(spotId, date));
	}

	@Override
	public void upsert(LocalDate startDate, LocalDate endDate) {
		List<SurfingItem> items = new ArrayList<>();
		initApiData(new ParameterizedTypeReference<ApiResponse<SurfingItem>>() {
		}, items, startDate, endDate, FishingType.NONE);

		for (SurfingItem item : items) {
			OutdoorSpot outdoorSpot = createOutdoorSpot(item, FishingType.NONE);

			surfingRepository.upsertSurfing(outdoorSpot.getId(), DateUtils.parseDate(item.getPredcYmd()),
				TimePeriod.from(item.getPredcNoonSeCd()).name(), Float.parseFloat(item.getAvgWvhgt()),
				Float.parseFloat(item.getAvgWvpd()), Float.parseFloat(item.getAvgWspd()),
				Float.parseFloat(item.getAvgWtem()), TotalIndex.fromDescription(item.getTotalIndex()).name());
		}
	}

	@Override
	public void update(LocalDate startDate, LocalDate endDate) {
		for (Long spotId : surfingRepository.findByForecastDateBetween(startDate, endDate)) {
			OutdoorSpot outdoorSpot = outdoorSpotRepository.findById(spotId).orElseThrow();
			UvIndexItem uvIndex = getUvIndex(startDate, endDate, outdoorSpot.getLatitude().doubleValue(),
				outdoorSpot.getLongitude().doubleValue());
			for (int i = 0; i < uvIndex.getTime().size(); i++) {
				Float uvIndexValue = uvIndex.getUvIndexMax().get(i);
				LocalDate date = uvIndex.getTime().get(i);
				surfingRepository.updateUvIndex(uvIndexValue, spotId, date);
			}
		}
	}

	private List<ActivitySpotDetail> transform(List<Surfing> surfingForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (Surfing surfingForecast : surfingForecasts) {
			details.add(SpotDetailMapper.toDto(surfingForecast));
		}
		return details;
	}

}
