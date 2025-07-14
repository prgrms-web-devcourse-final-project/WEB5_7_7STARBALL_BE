package sevenstar.marineleisure.alert.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import sevenstar.marineleisure.alert.dto.vo.JellyfishRegionVO;

/**
 *
 * @param reportDate : 리포트 일자
 * @param regions : 지역별 해파리 발생리스트
 */
@Builder
public record JellyfishResponseDto(LocalDate reportDate, List<JellyfishRegionVO> regions) {
}
