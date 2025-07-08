package sevenstar.marineleisure.alert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.domain.JellyfishSpecies;
import sevenstar.marineleisure.alert.repository.JellyfishRegionDensityRepository;
import sevenstar.marineleisure.alert.repository.JellyfishSpeciesRepository;

@Service
@RequiredArgsConstructor
public class JellyfishService implements AlertService<JellyfishRegionDensity> {

	JellyfishRegionDensityRepository densityRepository;
	JellyfishSpeciesRepository speciesRepository;

	/**
	 * [GET] /alerts/jellyfish
	 * @return 지역별해파리 발생리스트
	 */
	@Override
	public List<JellyfishRegionDensity> search() {
		return List.of();
	}

	/**
	 *
	 * @param name : 이름으로 해파리종의 정보를 찾습니다.
	 * @return 해당 해파리 JellyfishSpecies객체
	 */
	public JellyfishSpecies searchByName(String name) {
		return speciesRepository.findByName(name).orElse(null);
	}
}
