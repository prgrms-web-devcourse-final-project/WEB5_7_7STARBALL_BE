package sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse;

import java.time.LocalDate;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Builder
public record ActivityDetailSurfingResponse(
    LocalDate forecastDate,
    String timePeriod,
    Float waveHeight,
    Float wavePeriod,
    Float windSpeed,
    Float seaTemp,
    TotalIndex totalIndex,
    Float uvIndex
) implements ActivityDetail {
}
