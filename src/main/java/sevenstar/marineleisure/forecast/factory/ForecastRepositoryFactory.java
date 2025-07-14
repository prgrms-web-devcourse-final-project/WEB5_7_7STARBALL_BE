package sevenstar.marineleisure.forecast.factory;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;
import sevenstar.marineleisure.spot.repository.ActivityRepository;

@Component
@RequiredArgsConstructor
public class ForecastRepositoryFactory {
	private final FishingRepository	fishingRepository;
	private final MudflatRepository mudflatRepository;
	private final ScubaRepository scubaRepository;
	private final SurfingRepository surfingRepository;

	private Map<ActivityCategory, ActivityRepository> repositoryMap;

	@PostConstruct
	public void init(){
		repositoryMap = new EnumMap<>(ActivityCategory.class);
		repositoryMap.put(ActivityCategory.FISHING, fishingRepository);
		repositoryMap.put(ActivityCategory.MUDFLAT, mudflatRepository);
		repositoryMap.put(ActivityCategory.SCUBA, scubaRepository);
		repositoryMap.put(ActivityCategory.SURFING, surfingRepository);
	}

	public ActivityRepository<?,?> getRepository(ActivityCategory category){
		ActivityRepository repository = repositoryMap.get(category);
		if (repository == null) {
			throw new CustomException(CommonErrorCode.INTERNET_SERVER_ERROR, "Unsupported category : " + category);
		}
		return repository;
	}
}
