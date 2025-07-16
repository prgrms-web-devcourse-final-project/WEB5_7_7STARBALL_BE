package sevenstar.marineleisure.activity.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActivityDetailRequest(
    BigDecimal latitude,
    BigDecimal longitude,
    LocalDate date
) {
}
