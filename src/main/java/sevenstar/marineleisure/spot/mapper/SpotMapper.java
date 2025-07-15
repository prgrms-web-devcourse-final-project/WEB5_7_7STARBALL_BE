package sevenstar.marineleisure.spot.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.domain.SpotViewQuartile;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;
import sevenstar.marineleisure.spot.dto.detail.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.projection.SpotDistanceProjection;

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
}

