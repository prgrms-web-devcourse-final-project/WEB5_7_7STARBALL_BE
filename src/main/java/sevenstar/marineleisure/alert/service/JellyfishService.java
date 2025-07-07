package sevenstar.marineleisure.alert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;

@Service
@RequiredArgsConstructor
public class JellyfishService implements AlertService<JellyfishRegionDensity> {

	/**
	 * [GET] /alerts/jellyfish
	 * @return 지역별해파리 발생리스트
	 */
	@Override
	public List<JellyfishRegionDensity> search() {
		return List.of();
	}
}
