package sevenstar.marineleisure.activity.dto.reponse;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.TotalIndex;

@Builder
public record ActivitySummaryResponse(
    String spotName,
    TotalIndex totalIndex
) {
}
