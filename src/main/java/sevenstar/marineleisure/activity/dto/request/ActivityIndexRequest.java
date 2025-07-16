package sevenstar.marineleisure.activity.dto.request;

import java.math.BigDecimal;

public record ActivityIndexRequest(
    BigDecimal latitude,
    BigDecimal longitude,
    boolean global
) {

}