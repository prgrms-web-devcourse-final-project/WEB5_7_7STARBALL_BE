package sevenstar.marineleisure.spot.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.FishingTarget;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.FishingTargetRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.dto.SpotCreateRequest;
import sevenstar.marineleisure.spot.dto.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.SpotDistanceProjection;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;
import sevenstar.marineleisure.spot.mapper.SpotMapper;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;
import sevenstar.marineleisure.spot.repository.SpotViewStatsRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotServiceImpl implements SpotService {
	private final OutdoorSpotRepository outdoorSpotRepository;
	private final FishingRepository fishingRepository;
	private final FishingTargetRepository fishingTargetRepository;
	private final ScubaRepository scubaRepository;
	private final MudflatRepository mudflatRepository;
	private final SurfingRepository surfingRepository;
	private final SpotViewStatsRepository spotViewStatsRepository;

	@Override
	public SpotReadResponse searchSpot(Long userId, float latitude, float longitude, ActivityCategory category) {
		return search(
			outdoorSpotRepository.findBySpotDistanceInstanceByLatitudeAndLongitudeAndCategory(latitude, longitude,
				category.name()));
	}

	// TODO : exception, 조회도, favorite 여부 확인
	@Override
	public SpotReadResponse searchAllSpot(Long userId, float latitude, float longitude) {
		return search(outdoorSpotRepository.findBySpotDistanceInstanceByLatitudeAndLongitude(latitude, longitude));
	}

	private SpotReadResponse search(List<SpotDistanceProjection> spotDistanceProjections) {
		List<SpotReadResponse.SpotInfo> infos = new ArrayList<>();
		LocalDate now = LocalDate.now();

		for (SpotDistanceProjection spotDistanceProjection : spotDistanceProjections) {
			TotalIndex totalIndex = switch (ActivityCategory.valueOf(spotDistanceProjection.getCategory())) {
				case FISHING ->
					fishingRepository.findFishingForecasts(spotDistanceProjection.getId(), now, TimePeriod.PM)
						.orElseThrow()
						.getTotalIndex();
				case SCUBA -> scubaRepository.findFishingForecasts(spotDistanceProjection.getId(), now, TimePeriod.PM)
					.orElseThrow()
					.getTotalIndex();
				case MUDFLAT -> mudflatRepository.findBySpotIdAndForecastDate(spotDistanceProjection.getId(), now)
					.orElseThrow()
					.getTotalIndex();
				case SURFING ->
					surfingRepository.findFishingForecasts(spotDistanceProjection.getId(), now, TimePeriod.PM)
						.orElseThrow()
						.getTotalIndex();
			};

			String crowedLevel = "임시 데이터";
			boolean isFavorite = false;

			infos.add(SpotMapper.toDto(spotDistanceProjection, totalIndex.getDescription(), crowedLevel, isFavorite));
		}

		return new SpotReadResponse(infos);
	}

	@Override
	public <T> SpotDetailReadResponse<T> searchSpotDetail(Long spotId) {
		OutdoorSpot outdoorSpot = outdoorSpotRepository.findById(spotId).orElseThrow();
		LocalDate now = LocalDate.now();
		boolean isFavorite = false;
		return SpotMapper.toDto(outdoorSpot, isFavorite, getActivityDetail(outdoorSpot, now, now.plusDays(3)));
	}

	private List<Object> getActivityDetail(OutdoorSpot outdoorSpot, LocalDate startDate, LocalDate endDate) {
		List<Object> result = new ArrayList<>();
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			if (outdoorSpot.getCategory() == ActivityCategory.FISHING) {
				Fishing fishing = fishingRepository.findFishingForecasts(outdoorSpot.getId(), date, TimePeriod.PM)
					.orElseThrow();
				FishingTarget fishingTarget = fishingTargetRepository.findById(fishing.getTargetId()).orElseThrow();
				result.add(SpotMapper.toDto(fishing, fishingTarget));

			} else if (outdoorSpot.getCategory() == ActivityCategory.SCUBA) {
				Scuba scuba = scubaRepository.findFishingForecasts(outdoorSpot.getId(), date, TimePeriod.PM)
					.orElseThrow();
				result.add(SpotMapper.toDto(scuba));
			} else if (outdoorSpot.getCategory() == ActivityCategory.MUDFLAT) {
				Mudflat mudflat = mudflatRepository.findBySpotIdAndForecastDate(outdoorSpot.getId(), date)
					.orElseThrow();
				result.add(SpotMapper.toDto(mudflat));
			} else if (outdoorSpot.getCategory() == ActivityCategory.SURFING) {
				Surfing surfing = surfingRepository.findFishingForecasts(outdoorSpot.getId(), date, TimePeriod.PM)
					.orElseThrow();
				result.add(SpotMapper.toDto(surfing));
			}
		}
		return result;
	}

	@Override
	@Transactional
	public void createOutdoorSpot(SpotCreateRequest spotCreateRequest) {
		outdoorSpotRepository.save(SpotMapper.toEntity(spotCreateRequest));
	}

	@Override
	@Transactional
	public void upsertSpotViewStats(Long spotId) {
		spotViewStatsRepository.upsertViewStats(spotId, LocalDate.now());
	}
}
