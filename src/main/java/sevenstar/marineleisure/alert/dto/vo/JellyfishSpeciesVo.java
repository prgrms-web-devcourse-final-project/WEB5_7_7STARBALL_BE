package sevenstar.marineleisure.alert.dto.vo;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.DensityLevel;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

/**
 *
 * @param name : 해파리 이름
 * @param toxicity : 독성
 * @param density : 밀도
 */
@Builder
public record JellyfishSpeciesVo(String name, ToxicityLevel toxicity, DensityLevel density) {
}
