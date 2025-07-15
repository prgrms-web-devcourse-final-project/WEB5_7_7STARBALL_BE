package sevenstar.marineleisure.spot.service;

import static sevenstar.marineleisure.global.api.scheduler.SchedulerService.*;
import static sevenstar.marineleisure.global.util.CurrentUserUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.favorite.repository.FavoriteRepository;
import sevenstar.marineleisure.forecast.factory.ForecastRepositoryFactory;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.SpotErrorCode;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.domain.SpotViewQuartile;
import sevenstar.marineleisure.spot.dto.SpotPreviewReadResponse;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;
import sevenstar.marineleisure.spot.dto.detail.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivityDetailProviderFactory;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivitySpotDetail;
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
	private final SpotViewStatsRepository spotViewStatsRepository;
	private final SpotViewQuartileRepository spotViewQuartileRepository;
	private final FavoriteRepository favoriteRepository;
	private final ForecastRepositoryFactory forecastRepositoryFactory;
	private final ActivityDetailProviderFactory activityDetailProviderFactory;

	@Override
	public SpotReadResponse searchSpot(float latitude, float longitude, Integer radius, ActivityCategory category) {
		return search(outdoorSpotRepository.findSpots(latitude, longitude, radius * 1000,
			category != null ? category.name() : null));
	}

	private SpotReadResponse search(List<SpotDistanceProjection> spotDistanceProjections) {
		List<SpotReadResponse.SpotInfo> infos = new ArrayList<>();
		LocalDate now = LocalDate.now();

		for (SpotDistanceProjection spotDistanceProjection : spotDistanceProjections) {
			TotalIndex totalIndex = getTotalIndex(spotDistanceProjection.getId(), now,
				ActivityCategory.parse(spotDistanceProjection.getCategory()));
			SpotViewQuartile spotViewQuartile = spotViewQuartileRepository.findBySpotId(spotDistanceProjection.getId())
				.orElseGet(() -> new SpotViewQuartile(1, 1));
			boolean isFavorite = checkFavoriteSpot(spotDistanceProjection.getId());

			infos.add(SpotMapper.toDto(spotDistanceProjection, totalIndex, spotViewQuartile, isFavorite));
		}

		return new SpotReadResponse(infos);
	}

	private TotalIndex getTotalIndex(Long spotId, LocalDate date, ActivityCategory category) {
		return forecastRepositoryFactory.getRepository(category)
			.findTotalIndex(spotId, date, Pageable.ofSize(1)).stream().findFirst().orElse(TotalIndex.NONE);
	}

	@Override
	public <T extends ActivitySpotDetail> SpotDetailReadResponse<T> searchSpotDetail(Long spotId) {
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
			result.addAll(activityDetailProviderFactory.getProvider(outdoorSpot.getCategory()).getDetails(
				outdoorSpot.getId(), date));
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
