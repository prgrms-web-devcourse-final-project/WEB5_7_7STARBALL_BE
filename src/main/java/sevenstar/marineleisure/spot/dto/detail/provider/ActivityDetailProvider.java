package sevenstar.marineleisure.spot.dto.detail.provider;

import java.time.LocalDate;
import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

public interface ActivityDetailProvider {
	ActivityCategory getSupportCategory();

	ActivityRepository getSupportRepository();

	List<ActivitySpotDetail> getDetails(Long spotId, LocalDate date);
}
