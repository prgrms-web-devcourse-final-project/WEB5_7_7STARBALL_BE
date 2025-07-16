package sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Builder
public record ActivityDetailScubaResponse(
    LocalDate forecastDate,
    String timePeriod,
    LocalTime sunrise,
    LocalTime sunset,
    TidePhase tide,
    TotalIndex totalIndex,
    Float waveHeightMin,
    Float waveHeightMax,
    Float seaTempMin,
    Float seaTempMax,
    Float currentSpeedMin,
    Float currentSpeedMax
) implements ActivityDetail {
}
