package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;

public interface ActivityDetailProvider {
	ActivityCategory getSupportCategory();

	List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date);
}
