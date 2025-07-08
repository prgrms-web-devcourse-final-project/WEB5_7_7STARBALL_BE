package sevenstar.marineleisure.alert.mapper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.dto.response.JellyfishResponseDto;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishData;

@Component
@RequiredArgsConstructor
public class AlertMapper {
	public JellyfishResponseDto toDto(JellyfishRegionDensity jellyfishRegionDensity) {
		return JellyfishResponseDto.builder()
			.build();
	}

	public JellyfishRegionDensity toRegionDensityEntity(ParsedJellyfishData data, Long speciesId) {
		return JellyfishRegionDensity.builder()
			.regionName(data.getSpecies())
			.species(speciesId)
			.reportDate(data.getReportDate())
			.densityType(data.getDensityType())
			.build();
	}
}
