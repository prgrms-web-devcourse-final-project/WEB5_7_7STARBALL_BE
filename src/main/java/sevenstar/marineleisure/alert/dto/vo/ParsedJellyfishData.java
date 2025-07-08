package sevenstar.marineleisure.alert.dto.vo;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import sevenstar.marineleisure.global.enums.DensityLevel;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

@Getter
@Builder
public class ParsedJellyfishData {
	private String species;
	private String region;
	private LocalDate reportDate;
	private DensityLevel densityType;
	private ToxicityLevel toxicity;
}
