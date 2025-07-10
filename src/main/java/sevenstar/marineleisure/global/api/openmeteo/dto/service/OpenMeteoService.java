package sevenstar.marineleisure.global.api.openmeteo.dto.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.api.openmeteo.OpenMeteoApiClient;
import sevenstar.marineleisure.global.api.openmeteo.dto.common.OpenMeteoReadResponse;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.SunTimeItem;
import sevenstar.marineleisure.global.api.openmeteo.dto.item.UvIndexItem;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Service
@RequiredArgsConstructor
public class OpenMeteoService {
	private final OpenMeteoApiClient openMeteoApiClient;
	private final OutdoorSpotRepository outdoorSpotRepository;
	private final FishingRepository fishingRepository;
	private final MudflatRepository mudflatRepository;
	private final ScubaRepository scubaRepository;
	private final SurfingRepository surfingRepository;

	// TODO : exception , refactoring
	@Transactional
	public void updateApi(LocalDate startDate, LocalDate endDate) {
		// update fishing uvIndex
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

		// update mudflat uvIndex
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

		// update scuba sunrise and sunset
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

		// update surfing uvIndex
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

	private SunTimeItem getSunTimes(LocalDate startDate, LocalDate endDate, double latitude, double longitude) {
		ResponseEntity<OpenMeteoReadResponse<SunTimeItem>> response = openMeteoApiClient.getSunTimes(
			new ParameterizedTypeReference<>() {
			}, startDate, endDate, latitude, longitude);
		return response.getBody().getDaily();
	}

	private UvIndexItem getUvIndex(LocalDate startDate, LocalDate endDate, double latitude, double longitude) {
		ResponseEntity<OpenMeteoReadResponse<UvIndexItem>> response = openMeteoApiClient.getUvIndex(
			new ParameterizedTypeReference<>() {
			}, startDate, endDate, latitude, longitude);
		return response.getBody().getDaily();
	}
}
