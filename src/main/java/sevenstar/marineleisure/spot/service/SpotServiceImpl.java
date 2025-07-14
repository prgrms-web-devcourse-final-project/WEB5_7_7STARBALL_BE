package sevenstar.marineleisure.spot.service;

import static sevenstar.marineleisure.global.api.scheduler.SchedulerService.*;
import static sevenstar.marineleisure.global.util.CurrentUserUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.favorite.repository.FavoriteRepository;
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
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.SpotErrorCode;
import sevenstar.marineleisure.global.utils.FakeUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.domain.SpotViewQuartile;
import sevenstar.marineleisure.spot.dto.FishingReadResponse;
import sevenstar.marineleisure.spot.dto.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.SpotPreviewReadResponse;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;
import sevenstar.marineleisure.spot.dto.projection.SpotDistanceProjection;
import sevenstar.marineleisure.spot.dto.projection.SpotPreviewProjection;
import sevenstar.marineleisure.spot.mapper.SpotMapper;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;
import sevenstar.marineleisure.spot.repository.SpotViewQuartileRepository;
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
	private final SpotViewQuartileRepository spotViewQuartileRepository;
	private final FavoriteRepository favoriteRepository;

	@Override
	public SpotReadResponse searchSpot(float latitude, float longitude, Integer radius, ActivityCategory category) {
		return search(
			outdoorSpotRepository.findBySpotDistanceInstanceByLatitudeAndLongitudeAndCategory(latitude, longitude,
				radius * 1000, category.name()));
	}

	@Override
	public SpotReadResponse searchAllSpot(float latitude, float longitude, Integer radius) {
		return search(
			outdoorSpotRepository.findBySpotDistanceInstanceByLatitudeAndLongitude(latitude, longitude, radius * 1000));
	}

	private SpotReadResponse search(List<SpotDistanceProjection> spotDistanceProjections) {
		List<SpotReadResponse.SpotInfo> infos = new ArrayList<>();
		LocalDate now = LocalDate.now();

		for (SpotDistanceProjection spotDistanceProjection : spotDistanceProjections) {
			TotalIndex totalIndex = switch (ActivityCategory.parse(spotDistanceProjection.getCategory())) {
				case FISHING -> fishingRepository.findTotalIndexBySpotIdAndDate(spotDistanceProjection.getId(), now,
						TimePeriod.AM)
					.orElse(TotalIndex.NONE);
				case SCUBA ->
					scubaRepository.findTotalIndexBySpotIdAndDate(spotDistanceProjection.getId(), now, TimePeriod.AM)
						.orElse(TotalIndex.NONE);
				case MUDFLAT -> mudflatRepository.findTotalIndexBySpotIdAndDate(spotDistanceProjection.getId(), now)
					.orElse(TotalIndex.NONE);
				case SURFING ->
					surfingRepository.findTotalIndexBySpotIdAndDate(spotDistanceProjection.getId(), now, TimePeriod.AM)
						.orElse(TotalIndex.NONE);
			};

			SpotViewQuartile spotViewQuartile = spotViewQuartileRepository.findBySpotId(spotDistanceProjection.getId())
				.orElseGet(() -> new SpotViewQuartile(1, 1));

			boolean isFavorite = checkFavoriteSpot(spotDistanceProjection.getId());

			infos.add(
				SpotMapper.toDto(spotDistanceProjection, totalIndex, spotViewQuartile, isFavorite));
		}

		return new SpotReadResponse(infos);
	}

	@Override
	public <T> SpotDetailReadResponse<T> searchSpotDetail(Long spotId) {
		OutdoorSpot outdoorSpot = outdoorSpotRepository.findById(spotId)
			.orElseThrow(() -> new CustomException(SpotErrorCode.SPOT_NOT_FOUND));
		LocalDate now = LocalDate.now();

		boolean isFavorite = checkFavoriteSpot(spotId);

		return SpotMapper.toDto(outdoorSpot, isFavorite,
			getActivityDetail(outdoorSpot, now, now.plusDays(MAX_UPDATE_DAY)));
	}

	private List<Object> getActivityDetail(OutdoorSpot outdoorSpot, LocalDate startDate, LocalDate endDate) {
		List<Object> result = new ArrayList<>();
		for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
			switch (outdoorSpot.getCategory()) {
				case FISHING -> {
					List<FishingReadResponse> fishingForecasts = fishingRepository.findFishingForecasts(
						outdoorSpot.getId(), date);
					result.addAll(SpotMapper.toFishingSpotDetails(fishingForecasts));
				}
				case SURFING -> {
					List<Surfing> surfingForecasts = surfingRepository.findSurfingForecasts(outdoorSpot.getId(), date);
					result.addAll(SpotMapper.toSurfingSpotDetails(surfingForecasts));
				}
				case SCUBA -> {
					List<Scuba> scubaForecasts = scubaRepository.findScubaForecasts(outdoorSpot.getId(), date);
					result.addAll(SpotMapper.toScubaSpotDetails(scubaForecasts));
				}
				case MUDFLAT -> {
					Mudflat mudflat = mudflatRepository.findBySpotIdAndForecastDate(outdoorSpot.getId(), date)
						.orElseGet(() -> FakeUtils.fakeMudflat(outdoorSpot.getId()));
					result.add(SpotMapper.toMudflatSpotDetails(mudflat));
				}
			}
		}
		return result;
	}

	private boolean checkFavoriteSpot(Long spotId) {
		try {
			return favoriteRepository.existsByMemberIdAndSpotId(getCurrentUserId(), spotId);
		} catch (CustomException e) {
			return false;
		}
	}

	@Override
	public SpotPreviewReadResponse preview(float latitude, float longitude) {
		LocalDate now = LocalDate.now();
		// TODO : 기능 고도화 필요
		SpotPreviewProjection bestSpotInFishing = outdoorSpotRepository.findBestSpotInFishing(latitude, longitude, now);
		SpotPreviewProjection bestSpotInMudflat = outdoorSpotRepository.findBestSpotInMudflat(latitude, longitude, now);
		SpotPreviewProjection bestSpotInScuba = outdoorSpotRepository.findBestSpotInScuba(latitude, longitude, now);
		SpotPreviewProjection bestSpotInSurfing = outdoorSpotRepository.findBestSpotInSurfing(latitude, longitude, now);

		return new SpotPreviewReadResponse(SpotPreviewReadResponse.SpotPreview.from(bestSpotInFishing),
			SpotPreviewReadResponse.SpotPreview.from(bestSpotInMudflat),
			SpotPreviewReadResponse.SpotPreview.from(bestSpotInScuba),
			SpotPreviewReadResponse.SpotPreview.from(bestSpotInSurfing));
	}

	@Override
	@Transactional
	public void upsertSpotViewStats(Long spotId) {
		spotViewStatsRepository.upsertViewStats(spotId, LocalDate.now());
	}

}
