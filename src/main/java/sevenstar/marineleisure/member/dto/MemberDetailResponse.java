package sevenstar.marineleisure.member.dto;

import lombok.Builder;
import lombok.Getter;
import sevenstar.marineleisure.global.enums.MemberStatus;

import java.math.BigDecimal;

/**
 * 회원 상세 정보 응답 DTO
 */
@Getter
@Builder
public class MemberDetailResponse {
	private Long id;
	private String email;
	private String nickname;
	private MemberStatus status;
	private BigDecimal latitude;
	private BigDecimal longitude;
}