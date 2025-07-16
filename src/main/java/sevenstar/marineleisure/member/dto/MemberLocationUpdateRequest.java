package sevenstar.marineleisure.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 회원 위치 정보 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLocationUpdateRequest {
    private BigDecimal latitude;
    private BigDecimal longitude;
}