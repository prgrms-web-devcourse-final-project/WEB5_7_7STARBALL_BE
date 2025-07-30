package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.global.api.khoa.dto.common.ApiResponse;
import sevenstar.marineleisure.global.api.khoa.dto.item.MudflatItem;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.UvIndexItem;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.dto.EmailContent;
import sevenstar.marineleisure.spot.mapper.SpotDetailMapper;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class MudflatProvider extends ActivityProvider {
	private final MudflatRepository mudflatRepository;

	@Override
	public ActivityCategory getSupportCategory() {
		return ActivityCategory.MUDFLAT;
	}

	@Override
	public ActivityRepository getSupportRepository() {
		return mudflatRepository;
	}

	@Override
	public List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date) {
		return transform(mudflatRepository.findForecasts(spotId, date));
	}

	@Override
	public void upsert(LocalDate startDate, LocalDate endDate) {
		List<MudflatItem> items = new ArrayList<>();
		initApiData(new ParameterizedTypeReference<ApiResponse<MudflatItem>>() {
		}, items, startDate, endDate, FishingType.NONE);

		for (MudflatItem item : items) {
			OutdoorSpot outdoorSpot = createOutdoorSpot(item, FishingType.NONE);

			mudflatRepository.upsertMudflat(outdoorSpot.getId(), DateUtils.parseDate(item.getPredcYmd()),
				LocalTime.parse(item.getMdftExprnBgngTm()), LocalTime.parse(item.getMdftExprnEndTm()),
				Float.parseFloat(item.getMinArtmp()), Float.parseFloat(item.getMaxArtmp()),
				Float.parseFloat(item.getMinWspd()), Float.parseFloat(item.getMaxWspd()), item.getWeather(),
				TotalIndex.fromDescription(item.getTotalIndex()).name());
		}
	}

	@Override
	public void update(LocalDate startDate, LocalDate endDate) {
		for (Long spotId : mudflatRepository.findByForecastDateBetween(startDate, endDate)) {
			OutdoorSpot outdoorSpot = outdoorSpotRepository.findById(spotId).orElseThrow();
			UvIndexItem uvIndex = getUvIndex(startDate, endDate, outdoorSpot.getLatitude().doubleValue(),
				outdoorSpot.getLongitude().doubleValue());
			for (int i = 0; i < uvIndex.getTime().size(); i++) {
				Float uvIndexValue = uvIndex.getUvIndexMax().get(i);
				LocalDate date = uvIndex.getTime().get(i);
				mudflatRepository.updateUvIndex(uvIndexValue, spotId, date);
			}
		}
	}

	@Override
	public List<EmailContent> findEmailContent(TotalIndex totalIndex, LocalDate forecastDate) {
		return mudflatRepository.findEmailContentByTotalIndexAndForecastDate(totalIndex, forecastDate);
	}

	private List<ActivitySpotDetail> transform(List<Mudflat> mudflatForecasts) {
		List<ActivitySpotDetail> details = new ArrayList<>();
		for (Mudflat mudflatForecast : mudflatForecasts) {
			details.add(SpotDetailMapper.toDto(mudflatForecast));
		}
		return details;
	}
}
