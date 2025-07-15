package sevenstar.marineleisure.global.api.khoa.mapper;

import org.locationtech.jts.geom.Point;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.forecast.domain.FishingTarget;
import sevenstar.marineleisure.global.api.khoa.dto.item.KhoaItem;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;

@UtilityClass
public class KhoaMapper {
	/**
	 * dto => OutdoorSpot
	 * @param item api로 요청받은 response의 실제 데이터
	 * @param fishingType item이 FishingItem일 경우 ROCK / BOAT, 그 외일 경우 NONE
	 * @return
	 * @param <T> FishingItem / ScubaItem / SurfingItem / MudflatItem 중 하나
	 */
	public static <T extends KhoaItem> OutdoorSpot toEntity(T item, FishingType fishingType, Point point) {
		return OutdoorSpot.builder()
			.name(item.getLocation())
			.category(item.getCategory())
			.type(fishingType)
			.location(item.getLocation())
			.latitude(item.getLatitude())
			.longitude(item.getLongitude())
			.point(point)
			.build();
	}

	/**
	 * dto => FishTarget Entity
	 * @param name fish 이름
	 * @return
	 */
	public static FishingTarget toEntity(String name) {
		return new FishingTarget(name);
	}
}
