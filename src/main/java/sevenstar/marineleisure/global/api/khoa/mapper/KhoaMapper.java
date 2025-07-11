package sevenstar.marineleisure.global.api.khoa.mapper;

import java.time.LocalTime;

import org.locationtech.jts.geom.Point;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.FishingTarget;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.global.api.khoa.dto.item.FishingItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.KhoaItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.MudflatItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.ScubaItem;
import sevenstar.marineleisure.global.api.khoa.dto.item.SurfingItem;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.DateUtils;
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
	 * dto => Fishing Entity
	 * @param item api로 요청받은 response의 실제 데이터
	 * @param spotId outdoorSpot id
	 * @param targetId fishTarget id
	 * @return
	 */
	public static Fishing toEntity(FishingItem item, Long spotId, Long targetId) {
		return Fishing.builder()
			.spotId(spotId)
			.targetId(targetId)
			.forecastDate(DateUtils.parseDate(item.getPredcYmd()))
			.timePeriod(TimePeriod.from(item.getPredcNoonSeCd()))
			.tide(TidePhase.parse(item.getTdlvHrScr()))
			.totalIndex(TotalIndex.fromDescription(item.getTotalIndex()))
			.waveHeightMin(item.getMinWvhgt())
			.waveHeightMax(item.getMaxWvhgt())
			.seaTempMin(item.getMinWtem())
			.seaTempMax(item.getMaxWtem())
			.airTempMin(item.getMinArtmp())
			.airTempMax(item.getMinArtmp())
			.currentSpeedMin(item.getMinCrsp())
			.currentSpeedMax(item.getMaxCrsp())
			.windSpeedMin(item.getMinWspd())
			.windSpeedMax(item.getMaxWspd())
			.build();
	}

	/**
	 * dto => Surfing Entity
	 * @param item api로 요청받은 response의 실제 데이터
	 * @param spotId outdoorSpot id
	 * @return
	 */
	public static Surfing toEntity(SurfingItem item, Long spotId) {
		return Surfing.builder()
			.spotId(spotId)
			.forecastDate(DateUtils.parseDate(item.getPredcYmd()))
			.timePeriod(TimePeriod.from(item.getPredcNoonSeCd()))
			.waveHeight(Float.parseFloat(item.getAvgWvhgt()))
			.wavePeriod(Float.parseFloat(item.getAvgWvpd()))
			.windSpeed(Float.parseFloat(item.getAvgWspd()))
			.seaTemp(Float.parseFloat(item.getAvgWtem()))
			.totalIndex(TotalIndex.fromDescription(item.getTotalIndex()))
			.build();
	}

	/**
	 * dto => Scuba Entity
	 * @param item api로 요청받은 response의 실제 데이터
	 * @param spotId outdoorSpot id
	 * @return
	 */
	public static Scuba toEntity(ScubaItem item, Long spotId) {
		return Scuba.builder()
			.spotId(spotId)
			.forecastDate(DateUtils.parseDate(item.getPredcYmd()))
			.timePeriod(TimePeriod.from(item.getPredcNoonSeCd()))
			.tide(TidePhase.parse(item.getTdlvHrCn()))
			.totalIndex(TotalIndex.fromDescription(item.getTotalIndex()))
			.waveHeightMin(Float.parseFloat(item.getMinWvhgt()))
			.waveHeightMax(Float.parseFloat(item.getMaxWvhgt()))
			.seaTempMin(Float.parseFloat(item.getMinWtem()))
			.seaTempMax(Float.parseFloat(item.getMaxWtem()))
			.currentSpeedMin(Float.parseFloat(item.getMinCrsp()))
			.currentSpeedMax(Float.parseFloat(item.getMaxCrsp()))
			.build();
	}

	/**
	 * dto => Mudflat Entity
	 * @param item api로 요청받은 response의 실제 데이터
	 * @param spotId outdoorSpot id
	 * @return
	 */
	public static Mudflat toEntity(MudflatItem item, Long spotId) {
		return Mudflat.builder()
			.spotId(spotId)
			.forecastDate(DateUtils.parseDate(item.getPredcYmd()))
			.startTime(LocalTime.parse(item.getMdftExprnBgngTm()))
			.endTime(LocalTime.parse(item.getMdftExprnEndTm()))
			.airTempMin(Float.parseFloat(item.getMinArtmp()))
			.airTempMax(Float.parseFloat(item.getMaxArtmp()))
			.windSpeedMin(Float.parseFloat(item.getMinWspd()))
			.windSpeedMax(Float.parseFloat(item.getMaxWspd()))
			.weather(item.getWeather())
			.totalIndex(TotalIndex.fromDescription(item.getTotalIndex()))
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
