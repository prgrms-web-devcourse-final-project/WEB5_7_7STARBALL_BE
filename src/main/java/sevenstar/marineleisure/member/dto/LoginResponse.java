package sevenstar.marineleisure.member.dto;

import lombok.Builder;
import sevenstar.marineleisure.member.domain.Member;

/**
 * 로그인 성공 시 반환되는 DTO
 * Access 토큰과 사용자 정보를 포함
 * Refresh 토큰은 jwt.use-cookie 설정에 따라 쿠키 또는 응답 본문으로 전송
 * @param accessToken
 * @param userId
 * @param email
 * @param nickname
 * @param refreshToken jwt.use-cookie=false 설정일 때만 사용 (쿠키 대신 응답 본문에 포함)
 */
@Builder
public record LoginResponse(
	String accessToken,
	Long userId,
	String email,
	String nickname,
	String refreshToken
) {
	/**
	 * 쿠키 방식 사용 시 (jwt.use-cookie=true) 생성자
	 */
	public static LoginResponse of(String accessToken, Long userId, String email, String nickname) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.userId(userId)
			.email(email)
			.nickname(nickname)
			.build();
	}

	/**
	 * JSON 응답 방식 사용 시 (jwt.use-cookie=false) 생성자
	 */
	public static LoginResponse of(String accessToken, Long userId, String email, String nickname, String refreshToken) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.userId(userId)
			.email(email)
			.nickname(nickname)
			.refreshToken(refreshToken)
			.build();
	}

	/**
	 * 사용자 정보와 액세스 토큰만으로 생성하는 편의 메서드
	 */
	public static LoginResponse of(String accessToken, Member member) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.userId(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.build();
	}

	/**
	 * 사용자 정보와 액세스 토큰, 리프레시 토큰으로 생성하는 편의 메서드 (jwt.use-cookie=false 설정용)
	 */
	public static LoginResponse of(String accessToken, Member member, String refreshToken) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.userId(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.refreshToken(refreshToken)
			.build();
	}
}
