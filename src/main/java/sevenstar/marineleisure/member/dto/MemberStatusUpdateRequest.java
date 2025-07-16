package sevenstar.marineleisure.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import sevenstar.marineleisure.global.enums.MemberStatus;

/**
 * 회원 상태 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatusUpdateRequest {
    private MemberStatus status;
}