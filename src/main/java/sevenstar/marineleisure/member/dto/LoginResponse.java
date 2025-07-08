package sevenstar.marineleisure.member.dto;

import lombok.Builder;

/**
 * 로그인 성공 시 반환되는 DTO
 * Access 토큰과 사용자 정보를 포함
 * Refresh 토큰은 쿠키로 전송
 * @param accessToken
 * @param userId
 * @param email
 * @param nickname
 */
@Builder
public record LoginResponse(
        String accessToken,
        Long userId,
        String email,
        String nickname
) {
}
