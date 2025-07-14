package sevenstar.marineleisure.spot.mapper;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.domain.SpotViewQuartile;
import sevenstar.marineleisure.spot.dto.detail.FishingReadResponse;
import sevenstar.marineleisure.spot.dto.detail.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.projection.SpotDistanceProjection;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;

@UtilityClass
public class SpotMapper {
	public static SpotReadResponse.SpotInfo toDto(SpotDistanceProjection spotDistanceProjection, TotalIndex totalIndex,
		SpotViewQuartile spotViewQuartile, boolean isFavorite) {
		return new SpotReadResponse.SpotInfo(spotDistanceProjection.getId(), spotDistanceProjection.getName(),
			ActivityCategory.parse(spotDistanceProjection.getCategory()),
			spotDistanceProjection.getLatitude().floatValue(), spotDistanceProjection.getLongitude().floatValue(),
			spotDistanceProjection.getDistance().floatValue(), totalIndex, spotViewQuartile.getMonthQuartile(),
			spotViewQuartile.getWeekQuartile(), isFavorite);
	}

	public static <T> SpotDetailReadResponse toDto(OutdoorSpot outdoorSpot, boolean isFavorite, List<T> detail) {
		return new SpotDetailReadResponse(outdoorSpot.getId(), outdoorSpot.getName(), outdoorSpot.getCategory(),
			outdoorSpot.getLatitude().floatValue(), outdoorSpot.getLongitude().floatValue(), isFavorite, detail);
	}

	public static List<SpotDetailReadResponse.FishingSpotDetail> toFishingSpotDetails(
		List<FishingReadResponse> fishingForecasts) {
		List<SpotDetailReadResponse.FishingSpotDetail> detail = new ArrayList<>();
		for (FishingReadResponse fishing : fishingForecasts) {
			detail.add(new SpotDetailReadResponse.FishingSpotDetail(fishing.forecastDate(), fishing.timePeriod(),
				fishing.tide().getDescription(), fishing.totalIndex(),
				new SpotDetailReadResponse.RangeDetail(fishing.waveHeightMin(), fishing.waveHeightMax()),
				new SpotDetailReadResponse.RangeDetail(fishing.seaTempMin(), fishing.seaTempMax()),
				new SpotDetailReadResponse.RangeDetail(fishing.airTempMin(), fishing.airTempMax()),
				new SpotDetailReadResponse.RangeDetail(fishing.currentSpeedMin(), fishing.currentSpeedMax()),
				new SpotDetailReadResponse.RangeDetail(fishing.windSpeedMin(), fishing.windSpeedMax()),
				fishing.uvIndex().intValue(),
				new SpotDetailReadResponse.FishDetail(fishing.targetId(), fishing.targetName())));
		}
		return detail;
	}

	public static List<SpotDetailReadResponse.SurfingSpotDetail> toSurfingSpotDetails(List<Surfing> surfingForecasts) {
		List<SpotDetailReadResponse.SurfingSpotDetail> detail = new ArrayList<>();
		for (Surfing surfing : surfingForecasts) {
			detail.add(new SpotDetailReadResponse.SurfingSpotDetail(surfing.getForecastDate(), surfing.getTimePeriod(),
				surfing.getWaveHeight(), surfing.getWavePeriod().intValue(), surfing.getWindSpeed(),
				surfing.getSeaTemp(), surfing.getTotalIndex(), surfing.getUvIndex().intValue()));
		}
		return detail;
	}

	public static SpotDetailReadResponse.MudflatSpotDetail toMudflatSpotDetails(Mudflat mudflat) {
		return new SpotDetailReadResponse.MudflatSpotDetail(mudflat.getForecastDate().toString(),
			DateUtils.formatTime(mudflat.getStartTime()), DateUtils.formatTime(mudflat.getEndTime()),
			new SpotDetailReadResponse.RangeDetail(mudflat.getAirTempMin(), mudflat.getAirTempMax()),
			new SpotDetailReadResponse.RangeDetail(mudflat.getWindSpeedMin(), mudflat.getWindSpeedMax()),
			mudflat.getWeather(), mudflat.getTotalIndex(), mudflat.getUvIndex().intValue());
	}

	public static List<SpotDetailReadResponse.ScubaSpotDetail> toScubaSpotDetails(List<Scuba> scubaForecasts) {
		List<SpotDetailReadResponse.ScubaSpotDetail> detail = new ArrayList<>();
		for (Scuba scuba : scubaForecasts) {
			detail.add(new SpotDetailReadResponse.ScubaSpotDetail(scuba.getForecastDate(), scuba.getTimePeriod(),
				DateUtils.formatTime(scuba.getSunrise()), DateUtils.formatTime(scuba.getSunset()), scuba.getTide().getDescription(),
				new SpotDetailReadResponse.RangeDetail(scuba.getWaveHeightMin(), scuba.getWaveHeightMax()),
				new SpotDetailReadResponse.RangeDetail(scuba.getSeaTempMin(), scuba.getSeaTempMax()),
				new SpotDetailReadResponse.RangeDetail(scuba.getCurrentSpeedMin(), scuba.getCurrentSpeedMax()),
				scuba.getTotalIndex()));
		}
		return detail;
	}
}

