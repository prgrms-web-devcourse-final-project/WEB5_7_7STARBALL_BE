package sevenstar.marineleisure.alert.dto.vo;

import lombok.Builder;

/**
 *
 * @param name : 해파리 이름
 * @param toxicity : 독성
 * @param density : 밀도
 */
@Builder
public record JellyfishSpeciesVO(String name, String toxicity, String density) {
}
