package sevenstar.marineleisure.spot.dto.detail;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import sevenstar.marineleisure.global.enums.ActivityCategory;

@Component
public class ActivityDetailProviderFactory {
	private final Map<ActivityCategory, ActivityDetailProvider> providers;

	public ActivityDetailProviderFactory(List<ActivityDetailProvider> providers) {
		this.providers = providers;
	}
}
