package sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.mapper;

import org.springframework.stereotype.Component;

import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.ActivityDetailFishingResponse;
import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.ActivityDetailMudflatResponse;
import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.ActivityDetailScubaResponse;
import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.ActivityDetailSurfingResponse;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;

@Component
public class ActivityDetailMapper {
    public static ActivityDetailFishingResponse fromFishing(Fishing fishing) {
        return ActivityDetailFishingResponse.builder()
            .forecastDate(fishing.getForecastDate())
            .timePeriod(fishing.getTimePeriod().name())
            .tide(fishing.getTide())
            .totalIndex(fishing.getTotalIndex())
            .waveHeightMin(fishing.getWaveHeightMin())
            .waveHeightMax(fishing.getWaveHeightMax())
            .seaTempMin(fishing.getSeaTempMin())
            .seaTempMax(fishing.getSeaTempMax())
            .airTempMin(fishing.getAirTempMin())
            .airTempMax(fishing.getAirTempMax())
            .currentSpeedMin(fishing.getCurrentSpeedMin())
            .currentSpeedMax(fishing.getCurrentSpeedMax())
            .windSpeedMin(fishing.getWindSpeedMin())
            .windSpeedMax(fishing.getWindSpeedMax())
            .uvIndex(fishing.getUvIndex())
            .build();
    }

    public static ActivityDetailMudflatResponse fromMudflat(Mudflat mudflat) {
        return ActivityDetailMudflatResponse.builder()
            .forecastDate(mudflat.getForecastDate())
            .startTime(mudflat.getStartTime())
            .endTime(mudflat.getEndTime())
            .uvIndex(mudflat.getUvIndex())
            .airTempMin(mudflat.getAirTempMin())
            .airTempMax(mudflat.getAirTempMax())
            .windSpeedMin(mudflat.getWindSpeedMin())
            .windSpeedMax(mudflat.getWindSpeedMax())
            .weather(mudflat.getWeather())
            .totalIndex(mudflat.getTotalIndex())
            .build();
    }

    public static ActivityDetailSurfingResponse fromSurfing(Surfing surfing) {
        return ActivityDetailSurfingResponse.builder()
            .forecastDate(surfing.getForecastDate())
            .timePeriod(surfing.getTimePeriod().name())
            .waveHeight(surfing.getWaveHeight())
            .wavePeriod(surfing.getWavePeriod())
            .windSpeed(surfing.getWindSpeed())
            .seaTemp(surfing.getSeaTemp())
            .totalIndex(surfing.getTotalIndex())
            .uvIndex(surfing.getUvIndex())
            .build();
    }

    public static ActivityDetailScubaResponse fromScuba(Scuba scuba) {
        return ActivityDetailScubaResponse.builder()
            .forecastDate(scuba.getForecastDate())
            .timePeriod(scuba.getTimePeriod().name())
            .sunrise(scuba.getSunrise())
            .sunset(scuba.getSunset())
            .tide(scuba.getTide())
            .totalIndex(scuba.getTotalIndex())
            .waveHeightMin(scuba.getWaveHeightMin())
            .waveHeightMax(scuba.getWaveHeightMax())
            .seaTempMin(scuba.getSeaTempMin())
            .seaTempMax(scuba.getSeaTempMax())
            .currentSpeedMin(scuba.getCurrentSpeedMin())
            .currentSpeedMax(scuba.getCurrentSpeedMax())
            .build();
    }

}
