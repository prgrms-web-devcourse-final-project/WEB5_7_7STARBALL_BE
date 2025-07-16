package sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse;

import java.time.LocalDate;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Builder
public record ActivityDetailFishingResponse(
    LocalDate forecastDate,
    String timePeriod,
    TidePhase tide,
    TotalIndex totalIndex,
    Float waveHeightMin,
    Float waveHeightMax,
    Float seaTempMin,
    Float seaTempMax,
    Float airTempMin,
    Float airTempMax,
    Float currentSpeedMin,
    Float currentSpeedMax,
    Float windSpeedMin,
    Float windSpeedMax,
    Float uvIndex
) implements ActivityDetail {
}
