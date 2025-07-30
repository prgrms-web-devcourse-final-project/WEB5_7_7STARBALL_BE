package sevenstar.marineleisure.activity.dto.request;

import java.time.LocalDate;

public record ActivityDetailRequest(
    Float latitude,
	Float longitude,
    LocalDate time
) {
}
