package sevenstar.marineleisure.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 닉네임 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberNicknameUpdateRequest {
    private String nickname;
}