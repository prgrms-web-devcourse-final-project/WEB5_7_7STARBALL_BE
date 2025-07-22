package sevenstar.marineleisure.global.api.kakao.service;

import java.time.LocalDate;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.enums.Region;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.domain.BestSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;
import sevenstar.marineleisure.spot.repository.SpotPresetRepository;

@Service
@RequiredArgsConstructor
public class PresetSchedulerService {
	private static final double PRESET_RADIUS = 200_000;
	private final OutdoorSpotRepository outdoorSpotRepository;
	private final SpotPresetRepository spotPresetRepository;

	@Transactional
	public void updateRegionApi() {
		LocalDate now = LocalDate.now();
		BestSpot emptySpot = new BestSpot(-1L, "없는 지역입니다", TotalIndex.NONE);
		for (Region region : Region.getAllKoreaRegion()) {
			evictRegionCache(region);
			BestSpot bestSpotInFishing = outdoorSpotRepository.findBestSpotInFishing(region.getLatitude(),
				region.getLongitude(), now, PRESET_RADIUS).map(BestSpot::new).orElse(emptySpot);
			BestSpot bestSpotInMudflat = outdoorSpotRepository.findBestSpotInMudflat(region.getLatitude(),
				region.getLongitude(), now, PRESET_RADIUS).map(BestSpot::new).orElse(emptySpot);
			BestSpot bestSpotInScuba = outdoorSpotRepository.findBestSpotInScuba(region.getLatitude(),
				region.getLongitude(), now, PRESET_RADIUS).map(BestSpot::new).orElse(emptySpot);
			BestSpot bestSpotInSurfing = outdoorSpotRepository.findBestSpotInSurfing(region.getLatitude(),
				region.getLongitude(), now, PRESET_RADIUS).map(BestSpot::new).orElse(emptySpot);

			spotPresetRepository.upsert(region.name(), bestSpotInFishing.getSpotId(), bestSpotInFishing.getName(),
				bestSpotInFishing.getTotalIndex().name(), bestSpotInMudflat.getSpotId(), bestSpotInMudflat.getName(),
				bestSpotInMudflat.getTotalIndex().name(), bestSpotInScuba.getSpotId(), bestSpotInScuba.getName(),
				bestSpotInScuba.getTotalIndex().name(), bestSpotInSurfing.getSpotId(), bestSpotInSurfing.getName(),
				bestSpotInSurfing.getTotalIndex().name());
		}
	}

	@CacheEvict(value = "spotPresetPreviews", key = "#region.name()")
	public void evictRegionCache(Region region) {
		// 아무 동작 없음
	}
}
