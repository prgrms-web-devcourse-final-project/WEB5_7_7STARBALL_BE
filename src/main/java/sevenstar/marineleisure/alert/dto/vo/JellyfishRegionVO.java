package sevenstar.marineleisure.alert.dto.vo;

import lombok.Builder;

/**
 *
 * @param regionName : 발생지역
 * @param species : 해당 지역 발생 해파리정보
 */
@Builder
public record JellyfishRegionVO(String regionName, JellyfishSpeciesVO species) {
}
