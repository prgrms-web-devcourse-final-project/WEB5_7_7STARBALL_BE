package sevenstar.marineleisure.alert.mapper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.dto.response.JellyfishResponseDto;

/**
 * MarineLeisure - AlertMapper
 * create date:    25. 7. 7.
 * last update:    25. 7. 7.
 * author:  gigol
 * purpose: 
 */
@Component
@RequiredArgsConstructor
public class AlertMapper {
	public JellyfishResponseDto toDto(JellyfishRegionDensity jellyfishRegionDensity) {
		return JellyfishResponseDto.builder()
			.build();
	}
}
