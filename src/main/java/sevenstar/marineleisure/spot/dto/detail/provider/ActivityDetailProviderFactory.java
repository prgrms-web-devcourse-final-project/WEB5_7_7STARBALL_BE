package sevenstar.marineleisure.spot.dto.detail.provider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import sevenstar.marineleisure.global.enums.ActivityCategory;

@Component
public class ActivityDetailProviderFactory {
	private final Map<ActivityCategory, ActivityDetailProvider> providers = new EnumMap<>(ActivityCategory.class);
	private final List<ActivityDetailProvider> detailProviders;

	public ActivityDetailProviderFactory(List<ActivityDetailProvider> detailProviders) {
		this.detailProviders = detailProviders;
	}

	@PostConstruct
	public void init() {
		for (ActivityDetailProvider detailProvider : detailProviders) {
			providers.put(detailProvider.getSupportCategory(), detailProvider);
		}
	}

	public ActivityDetailProvider getProvider(ActivityCategory category) {
		return providers.get(category);
	}
}
