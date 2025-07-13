package sevenstar.marineleisure.activity.dto.request;

import java.math.BigDecimal;

public record ActivityDetailRequest(
    BigDecimal latitude,
    BigDecimal longitude
) {
}
