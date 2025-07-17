package sevenstar.marineleisure.spot.dto.detail.provider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import sevenstar.marineleisure.global.enums.ActivityCategory;

@Component
public class ActivityDetailProviderFactory {
	private final Map<ActivityCategory, ActivityProvider> providers = new EnumMap<>(ActivityCategory.class);
	private final List<ActivityProvider> detailProviders;

	public ActivityDetailProviderFactory(List<ActivityProvider> detailProviders) {
		this.detailProviders = detailProviders;
	}

	@PostConstruct
	public void init() {
		for (ActivityProvider detailProvider : detailProviders) {
			providers.put(detailProvider.getSupportCategory(), detailProvider);
		}
	}

	public ActivityProvider getProvider(ActivityCategory category) {
		return providers.get(category);
	}
}
