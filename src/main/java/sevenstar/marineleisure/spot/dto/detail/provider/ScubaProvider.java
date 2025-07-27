package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.ScubaItem;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.SunTimeItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.mapper.SpotDetailMapper;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class ScubaProvider extends ActivityProvider {
	private final ScubaRepository scubaRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.SCUBA;
	}

	@Override
	public ActivityRepository getSupportRepository() {
		return scubaRepository;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		return transform(scubaRepository.findForecasts(spotId, date));
	}

	@Override
	public void upsert(LocalDate startDate, LocalDate endDate) {
		List<ScubaItem> items = new ArrayList<>();
		initApiData(new ParameterizedTypeReference<ApiResponse<ScubaItem>>() {
		}, items, startDate, endDate, FishingType.NONE);

		for (ScubaItem item : items) {
			OutdoorSpot outdoorSpot = createOutdoorSpot(item, FishingType.NONE);
			scubaRepository.upsertScuba(outdoorSpot.getId(), DateUtils.parseDate(item.getPredcYmd()),
				TimePeriod.from(item.getPredcNoonSeCd()).name(), TidePhase.parse(item.getTdlvHrCn()).name(),
				TotalIndex.fromDescription(item.getTotalIndex()).name(), Float.parseFloat(item.getMinWvhgt()),
				Float.parseFloat(item.getMaxWvhgt()), Float.parseFloat(item.getMinWtem()),
				Float.parseFloat(item.getMaxWtem()), Float.parseFloat(item.getMinCrsp()),
				Float.parseFloat(item.getMaxCrsp()));
		}
	}

	@Override
	public void update(LocalDate startDate, LocalDate endDate) {
		for (Long spotId : scubaRepository.findByForecastDateBetween(startDate, endDate)) {
			OutdoorSpot outdoorSpot = outdoorSpotRepository.findById(spotId).orElseThrow();
			SunTimeItem sunTimeItem = getSunTimes(startDate, endDate, outdoorSpot.getLatitude().doubleValue(),
				outdoorSpot.getLongitude().doubleValue());
			for (int i = 0; i < sunTimeItem.getTime().size(); i++) {
				LocalDateTime sunrise = sunTimeItem.getSunrise().get(i);
				LocalDateTime sunset = sunTimeItem.getSunset().get(i);
				LocalDate date = sunTimeItem.getTime().get(i);
				scubaRepository.updateSunriseAndSunset(sunrise.toLocalTime(), sunset.toLocalTime(), spotId, date);
			}
		}
	}

	private List<ActivitySpotDetail> transform(List<Scuba> scubaForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (Scuba scubaForecast : scubaForecasts) {
			details.add(SpotDetailMapper.toDto(scubaForecast));
		}
		return details;
	}

}
