package sevenstar.marineleisure.activity.dto.request;

import java.math.BigDecimal;

public record ActivityWeatherRequest(
    BigDecimal latitude,
    BigDecimal longitude
) {
}