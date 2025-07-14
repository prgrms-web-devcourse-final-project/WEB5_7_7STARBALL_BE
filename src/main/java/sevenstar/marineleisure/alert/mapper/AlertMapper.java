package sevenstar.marineleisure.alert.mapper;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.dto.response.JellyfishResponseDto;
import sevenstar.marineleisure.alert.dto.vo.JellyfishDetailVO;
import sevenstar.marineleisure.alert.dto.vo.JellyfishRegionVO;
import sevenstar.marineleisure.alert.dto.vo.JellyfishSpeciesVO;
import sevenstar.marineleisure.global.enums.DensityLevel;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

@Component
@RequiredArgsConstructor
public class AlertMapper {

	public JellyfishResponseDto toResponseDto(List<JellyfishDetailVO> detailList) {
		if (detailList.isEmpty()) {
			return null;
		}
		LocalDate reportDate = detailList.get(0).getReportDate();

		List<JellyfishRegionVO> regions = detailList.stream()
			.map(detail -> new JellyfishRegionVO(
				detail.getRegion(),
				new JellyfishSpeciesVO(
					detail.getSpecies(),
					ToxicityLevel.valueOf(detail.getToxicity()).getDescription(),
					DensityLevel.valueOf(detail.getDensityType()).getDescription()
				)
			))
			.toList();

		return new JellyfishResponseDto(reportDate, regions);
	}
}
