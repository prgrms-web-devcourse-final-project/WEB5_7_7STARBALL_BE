package sevenstar.marineleisure.spot.mapper;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.global.utils.DateUtils;
import sevenstar.marineleisure.spot.dto.detail.items.FishDetail;
import sevenstar.marineleisure.spot.dto.detail.items.FishingSpotDetail;
import sevenstar.marineleisure.spot.dto.detail.items.MudflatSpotDetail;
import sevenstar.marineleisure.spot.dto.detail.items.RangeDetail;
import sevenstar.marineleisure.spot.dto.detail.items.ScubaSpotDetail;
import sevenstar.marineleisure.spot.dto.detail.items.SurfingSpotDetail;
import sevenstar.marineleisure.spot.dto.projection.FishingReadProjection;

@UtilityClass
public class SpotDetailMapper {
	public static FishingSpotDetail toDto(FishingReadProjection projection) {
		return new FishingSpotDetail(projection.getForecastDate(), projection.getTimePeriod(),
			projection.getTide().getDescription(), projection.getTotalIndex(),
			RangeDetail.of(projection.getWaveHeightMin(), projection.getWaveHeightMax()),
			RangeDetail.of(projection.getSeaTempMin(), projection.getSeaTempMax()),
			RangeDetail.of(projection.getAirTempMin(), projection.getAirTempMax()),
			RangeDetail.of(projection.getCurrentSpeedMin(), projection.getCurrentSpeedMax()),
			RangeDetail.of(projection.getWindSpeedMin(), projection.getWindSpeedMax()),
			projection.getUvIndex().intValue(), new FishDetail(projection.getTargetId(), projection.getTargetName()));
	}

	public static MudflatSpotDetail toDto(Mudflat mudflatForecast) {
		return new MudflatSpotDetail(mudflatForecast.getForecastDate(),
			DateUtils.formatTime(mudflatForecast.getStartTime()), DateUtils.formatTime(mudflatForecast.getEndTime()),
			RangeDetail.of(mudflatForecast.getAirTempMin(), mudflatForecast.getAirTempMax()),
			RangeDetail.of(mudflatForecast.getWindSpeedMin(), mudflatForecast.getWindSpeedMax()),
			mudflatForecast.getWeather(), mudflatForecast.getTotalIndex(), mudflatForecast.getUvIndex().intValue());
	}

	public static ScubaSpotDetail toDto(Scuba scubaForecast) {
		return new ScubaSpotDetail(scubaForecast.getForecastDate(), scubaForecast.getTimePeriod(),
			DateUtils.formatTime(scubaForecast.getSunrise()), DateUtils.formatTime(scubaForecast.getSunset()),
			scubaForecast.getTide().getDescription(),
			RangeDetail.of(scubaForecast.getWaveHeightMin(), scubaForecast.getWaveHeightMax()),
			RangeDetail.of(scubaForecast.getSeaTempMin(), scubaForecast.getSeaTempMax()),
			RangeDetail.of(scubaForecast.getCurrentSpeedMin(), scubaForecast.getCurrentSpeedMax()),
			scubaForecast.getTotalIndex());
	}

	public static SurfingSpotDetail toDto(Surfing surfingForecast) {
		return new SurfingSpotDetail(surfingForecast.getForecastDate(), surfingForecast.getTimePeriod(),
			surfingForecast.getWaveHeight(), surfingForecast.getWavePeriod().intValue(), surfingForecast.getWindSpeed(),
			surfingForecast.getSeaTemp(), surfingForecast.getTotalIndex(), surfingForecast.getUvIndex().intValue());
	}
}
