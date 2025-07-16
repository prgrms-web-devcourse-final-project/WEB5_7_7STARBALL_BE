package sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Builder
public record ActivityDetailMudflatResponse(
    LocalDate forecastDate,
    LocalTime startTime,
    LocalTime endTime,
    Float uvIndex,
    Float airTempMin,
    Float airTempMax,
    Float windSpeedMin,
    Float windSpeedMax,
    String weather,
    TotalIndex totalIndex
) implements ActivityDetail {
}
