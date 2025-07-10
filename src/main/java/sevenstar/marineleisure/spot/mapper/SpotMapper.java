package sevenstar.marineleisure.spot.mapper;

import java.math.BigDecimal;
import java.util.List;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.FishingTarget;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.domain.SpotViewQuartile;
import sevenstar.marineleisure.spot.dto.SpotCreateRequest;
import sevenstar.marineleisure.spot.dto.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.SpotDistanceProjection;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;

@UtilityClass
public class SpotMapper {
	public static SpotReadResponse.SpotInfo toDto(SpotDistanceProjection spotDistanceProjection, String currentStatus,
		SpotViewQuartile spotViewQuartile, boolean isFavorite) {
		return new SpotReadResponse.SpotInfo(spotDistanceProjection.getId(), spotDistanceProjection.getName(),
			ActivityCategory.valueOf(spotDistanceProjection.getCategory()),
			spotDistanceProjection.getLatitude().floatValue(), spotDistanceProjection.getLongitude().floatValue(),
			spotDistanceProjection.getDistance().floatValue(), currentStatus, spotViewQuartile.getMonthQuartile(),
			spotViewQuartile.getWeekQuartile(), isFavorite);
	}

	public static <T> SpotDetailReadResponse toDto(OutdoorSpot outdoorSpot, boolean isFavorite, List<T> detail) {
		return new SpotDetailReadResponse(outdoorSpot.getId(), outdoorSpot.getName(), outdoorSpot.getCategory(),
			outdoorSpot.getLocation(), outdoorSpot.getLatitude().floatValue(), outdoorSpot.getLongitude().floatValue(),
			isFavorite, detail);
	}

	public static SpotDetailReadResponse.FishingSpotDetail toDto(Fishing fishing, FishingTarget fishingTarget) {
		return new SpotDetailReadResponse.FishingSpotDetail(fishing.getForecastDate().toString(),
			fishing.getTimePeriod().name(), fishing.getTide().getDescription(),
			fishing.getTotalIndex().getDescription(),
			new SpotDetailReadResponse.RangeDetail(fishing.getWaveHeightMin(), fishing.getWaveHeightMax()),
			new SpotDetailReadResponse.RangeDetail(fishing.getSeaTempMin(), fishing.getSeaTempMax()),
			new SpotDetailReadResponse.RangeDetail(fishing.getAirTempMin(), fishing.getAirTempMax()),
			new SpotDetailReadResponse.RangeDetail(fishing.getCurrentSpeedMin(), fishing.getCurrentSpeedMax()),
			new SpotDetailReadResponse.RangeDetail(fishing.getWindSpeedMin(), fishing.getWindSpeedMax()),
			fishing.getUvIndex().intValue(),
			new SpotDetailReadResponse.FishDetail(fishingTarget.getId(), fishingTarget.getName()));
	}

	public static SpotDetailReadResponse.SurfingSpotDetail toDto(Surfing surfing) {
		return new SpotDetailReadResponse.SurfingSpotDetail(surfing.getForecastDate().toString(),
			surfing.getTimePeriod().name(), surfing.getWaveHeight(), surfing.getWavePeriod().intValue(),
			surfing.getWindSpeed(), surfing.getSeaTemp(), surfing.getTotalIndex().getDescription(),
			surfing.getUvIndex().intValue());
	}

	public static SpotDetailReadResponse.MudflatSpotDetail toDto(Mudflat mudflat) {
		return new SpotDetailReadResponse.MudflatSpotDetail(mudflat.getForecastDate().toString(),
			mudflat.getStartTime().toString(), mudflat.getEndTime().toString(),
			new SpotDetailReadResponse.RangeDetail(mudflat.getAirTempMin(), mudflat.getAirTempMax()),
			new SpotDetailReadResponse.RangeDetail(mudflat.getWindSpeedMin(), mudflat.getWindSpeedMax()),
			mudflat.getWeather(), mudflat.getTotalIndex().getDescription(), mudflat.getUvIndex().intValue());
	}

	public static SpotDetailReadResponse.ScubaSpotDetail toDto(Scuba scuba) {
		return new SpotDetailReadResponse.ScubaSpotDetail(scuba.getForecastDate().toString(),
			scuba.getTimePeriod().name(), scuba.getSunrise().toString(), scuba.getSunset().toString(), scuba
			.getTide().getDescription(),
			new SpotDetailReadResponse.RangeDetail(scuba.getWaveHeightMin(), scuba.getWaveHeightMax()),
			new SpotDetailReadResponse.RangeDetail(scuba.getSeaTempMin(), scuba.getSeaTempMax()),
			new SpotDetailReadResponse.RangeDetail(scuba.getCurrentSpeedMin(), scuba.getCurrentSpeedMax()),
			scuba.getTotalIndex().getDescription());
	}

	public static OutdoorSpot toEntity(SpotCreateRequest spotCreateRequest) {
		return OutdoorSpot.builder()
			.latitude(new BigDecimal(spotCreateRequest.latitude()))
			.longitude(new BigDecimal(spotCreateRequest.longitude()))
			.location(spotCreateRequest.location())
			.name(spotCreateRequest.location())
			.build();
	}
}

