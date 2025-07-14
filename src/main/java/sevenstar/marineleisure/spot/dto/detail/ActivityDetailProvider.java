package sevenstar.marineleisure.spot.dto.detail;

import java.time.LocalDate;
import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;

public interface ActivityDetailProvider {
	ActivityCategory getSupportCategory();

	List<ActivityDetailResponse> getDetails(Long spotId, LocalDate date);
}
