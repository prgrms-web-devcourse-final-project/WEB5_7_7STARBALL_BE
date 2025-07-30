package sevenstar.marineleisure.activity.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.activity.dto.reponse.ActivityDetailResponse;
import sevenstar.marineleisure.activity.dto.reponse.ActivitySummaryResponse;
import sevenstar.marineleisure.activity.dto.reponse.ActivityWeatherResponse;
import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.ActivityDetail;
import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.mapper.ActivityDetailMapper;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.dto.SpotPreviewReadResponse;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;
import sevenstar.marineleisure.spot.service.SpotService;

@Service
@RequiredArgsConstructor
public class ActivityService {

	private final OutdoorSpotRepository outdoorSpotRepository;

	private final FishingRepository fishingRepository;
	private final MudflatRepository mudflatRepository;
	private final ScubaRepository scubaRepository;
	private final SurfingRepository surfingRepository;

	private final SpotService spotService;

	@Transactional(readOnly = true)
	public Map<String, ActivitySummaryResponse> getActivitySummary(BigDecimal latitude, BigDecimal longitude,
		boolean global) {
		if (global) {
			return getGlobalActivitySummary();
		} else {
			return getLocalActivitySummary(latitude, longitude);
		}
	}

	private Map<String, ActivitySummaryResponse> getLocalActivitySummary(BigDecimal latitude, BigDecimal longitude) {
		Map<String, ActivitySummaryResponse> responses = new HashMap<>();

		SpotPreviewReadResponse preview = spotService.preview(latitude.floatValue(), longitude.floatValue());
		responses.put("Fishing",
			new ActivitySummaryResponse(preview.fishing().getName(), preview.fishing().getTotalIndex(),preview.fishing()
				.getSpotId()));
		responses.put("Mudflat",
			new ActivitySummaryResponse(preview.mudflat().getName(), preview.mudflat().getTotalIndex(),preview.mudflat()
				.getSpotId()));
		responses.put("Surfing",
			new ActivitySummaryResponse(preview.surfing().getName(), preview.surfing().getTotalIndex(),preview.surfing()
				.getSpotId()));
		responses.put("Scuba", new ActivitySummaryResponse(preview.scuba().getName(), preview.scuba().getTotalIndex(),preview.scuba()
			.getSpotId()));

		// Fishing fishingBySpot = null;
		// Mudflat mudflatBySpot = null;
		// Surfing surfingBySpot = null;
		// Scuba scubaBySpot = null;
		//
		// LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		// LocalDateTime endOfDay = startOfDay.plusDays(1);
		//
		// List<OutdoorSpot> outdoorSpotList = outdoorSpotRepository.findByCoordinates(latitude, longitude, 10);
		//
		// while (fishingBySpot == null || mudflatBySpot == null || surfingBySpot == null || scubaBySpot == null) {
		//
		//     OutdoorSpot currentSpot;
		//     Long currentSpotId;
		//
		//     try {
		//         currentSpot = outdoorSpotList.removeFirst();
		//         currentSpotId = currentSpot.getId();
		//     } catch (Exception e) {
		//         break;
		//     }
		//
		//     if (fishingBySpot == null) {
		//         Optional<Fishing> fishingResult = fishingRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		//             currentSpotId, startOfDay, endOfDay);
		//
		//         if (fishingResult.isPresent()) {
		//             fishingBySpot = fishingResult.get();
		//             responses.put("Fishing",
		//                 new ActivitySummaryResponse(currentSpot.getName(), fishingResult.get().getTotalIndex()));
		//         }
		//     }
		//
		//     if (mudflatBySpot == null) {
		//         Optional<Mudflat> mudflatResult = mudflatRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		//             currentSpotId, startOfDay, endOfDay);
		//
		//         if (mudflatResult.isPresent()) {
		//             mudflatBySpot = mudflatResult.get();
		//             responses.put("Mudflat",
		//                 new ActivitySummaryResponse(currentSpot.getName(), mudflatResult.get().getTotalIndex()));
		//         }
		//     }
		//
		//     if (surfingBySpot == null) {
		//         Optional<Surfing> surfingResult = surfingRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		//             currentSpotId, startOfDay, endOfDay);
		//
		//         if (surfingResult.isPresent()) {
		//             surfingBySpot = surfingResult.get();
		//             responses.put("Surfing",
		//                 new ActivitySummaryResponse(currentSpot.getName(), surfingResult.get().getTotalIndex()));
		//         }
		//     }
		//
		//     if (scubaBySpot == null) {
		//         Optional<Scuba> scubaResult = scubaRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
		//             currentSpotId, startOfDay, endOfDay);
		//
		//         if (scubaResult.isPresent()) {
		//             scubaBySpot = scubaResult.get();
		//             responses.put("Scuba",
		//                 new ActivitySummaryResponse(currentSpot.getName(), scubaResult.get().getTotalIndex()));
		//         }
		//     }
		// }

		return responses;
	}

	private Map<String, ActivitySummaryResponse> getGlobalActivitySummary() {
		Map<String, ActivitySummaryResponse> responses = new HashMap<>();

		LocalDate now = LocalDate.now();

		Optional<Fishing> fishingResult = fishingRepository.findBestTotaIndexFishing(now);
		Optional<Mudflat> mudflatResult = mudflatRepository.findBestTotaIndexMudflat(now);
		Optional<Surfing> surfingResult = surfingRepository.findBestTotaIndexSurfing(now);
		Optional<Scuba> scubaResult = scubaRepository.findBestTotaIndexScuba(now);

		if (fishingResult.isPresent()) {
			Fishing fishing = fishingResult.get();
			OutdoorSpot spot = outdoorSpotRepository.findById(fishing.getSpotId()).get();
			responses.put("Fishing", new ActivitySummaryResponse(spot.getName(), fishing.getTotalIndex(),fishing.getSpotId()));
		}

		if (mudflatResult.isPresent()) {
			Mudflat mudflat = mudflatResult.get();
			OutdoorSpot spot = outdoorSpotRepository.findById(mudflat.getSpotId()).get();
			responses.put("Mudflat", new ActivitySummaryResponse(spot.getName(), mudflat.getTotalIndex(),mudflat.getSpotId()));
		}

		if (scubaResult.isPresent()) {
			Scuba scuba = scubaResult.get();
			OutdoorSpot spot = outdoorSpotRepository.findById(scuba.getSpotId()).get();
			responses.put("Scuba", new ActivitySummaryResponse(spot.getName(), scuba.getTotalIndex(),scuba.getSpotId()));
		}

		if (surfingResult.isPresent()) {
			Surfing surfing = surfingResult.get();
			OutdoorSpot spot = outdoorSpotRepository.findById(surfing.getSpotId()).get();
			responses.put("Surfing", new ActivitySummaryResponse(spot.getName(), surfing.getTotalIndex(),surfing.getSpotId()));
		}

		return responses;
	}

	@Transactional(readOnly = true)
	public ActivityDetailResponse getActivityDetail(ActivityCategory activity, BigDecimal latitude,
		BigDecimal longitude) {

		OutdoorSpot nearSpot = outdoorSpotRepository.findNearSpot(latitude.floatValue(), longitude.floatValue(),
				activity.name())
			.orElseThrow(() -> new NoSuchElementException("가까운 지점을 찾을 수 없습니다."));

		LocalDate now = LocalDate.now();

		ActivityDetail result;

		switch (activity) {
			case FISHING -> {
				Fishing resultSearch = fishingRepository.findBySpotIdAndForecastDateAndTimePeriod(
					nearSpot.getId(), now, TimePeriod.AM).get();
				result = ActivityDetailMapper.fromFishing(resultSearch);
			}
			case MUDFLAT -> {
				Mudflat resultSearch = mudflatRepository.findBySpotIdAndForecastDate(
					nearSpot.getId(), now).get();
				result = ActivityDetailMapper.fromMudflat(resultSearch);
			}
			case SURFING -> {
				Surfing resultSearch = surfingRepository.findBySpotIdAndForecastDateAndTimePeriod(
					nearSpot.getId(), now, TimePeriod.AM).get();
				result = ActivityDetailMapper.fromSurfing(resultSearch);
			}
			case SCUBA -> {
				Scuba resultSearch = scubaRepository.findBySpotIdAndForecastDateAndTimePeriod(
					nearSpot.getId(), now, TimePeriod.AM).get();
				result = ActivityDetailMapper.fromScuba(resultSearch);
			}
			default -> {
				throw new RuntimeException("WRONG_ACTIVITY");
			}
		}

		return new ActivityDetailResponse(activity.toString(), nearSpot.getLocation(), result);
	}

	@Transactional(readOnly = true)
	public ActivityWeatherResponse getWeatherBySpot(Float latitude, Float longitude) {
		// 1. 가까운 낚시 지점 조회
		OutdoorSpot nearSpot = outdoorSpotRepository.findNearFishingSpot(latitude.doubleValue(),
				longitude.doubleValue())
			.orElseThrow(() -> new NoSuchElementException("가까운 낚시 지점을 찾을 수 없습니다."));

		// 2. 해당 지점의 예보 데이터 조회
		Fishing fishing = fishingRepository.findFishingBySpotIdAndForecastDateAndTimePeriod(nearSpot.getId(),
				LocalDate.now(),
				TimePeriod.AM)
			.orElseThrow(() -> new NoSuchElementException("해당 지점에 대한 예보 정보를 찾을 수 없습니다."));

		// 3. 결과 조합
		return new ActivityWeatherResponse(
			nearSpot.getName(),
			fishing.getWindSpeedMax().toString(),
			fishing.getWaveHeightMax().toString(),
			fishing.getSeaTempMax().toString(),
			nearSpot.getId()
		);
	}

}
