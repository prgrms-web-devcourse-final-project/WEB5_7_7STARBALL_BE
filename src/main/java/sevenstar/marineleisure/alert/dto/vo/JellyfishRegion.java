package sevenstar.marineleisure.alert.dto.vo;

import java.util.List;

import lombok.Builder;

/**
 *
 * @param regionName : 발생지역
 * @param species : 해당 지역 발생 해파리정보
 */
@Builder
public record JellyfishRegion(String regionName, List<JellyfishSpecies> species) {
}
