package sevenstar.marineleisure.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import sevenstar.marineleisure.global.jwt.UserPrincipal;

/**
 * 현재 인증된 사용자의 정보를 쉽게 접근할 수 있는 유틸리티 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrentUserUtil {

	/**
	 * 현재 인증된 사용자의 ID를 반환합니다.
	 * 인증되지 않은 경우 IllegalStateException을 발생시킵니다.
	 *
	 * @return 현재 인증된 사용자의 ID
	 * @throws IllegalStateException 인증되지 않은 경우
	 */
	public static Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
			!(authentication.getPrincipal() instanceof UserPrincipal)) {
			throw new IllegalStateException("인증된 사용자가 아닙니다.");
		}

		UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
		return principal.getId();
	}

	/**
	 * 현재 인증된 사용자의 이메일을 반환합니다.
	 * 인증되지 않은 경우 IllegalStateException을 발생시킵니다.
	 *
	 * @return 현재 인증된 사용자의 이메일
	 * @throws IllegalStateException 인증되지 않은 경우
	 */
	public static String getCurrentUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
			!(authentication.getPrincipal() instanceof UserPrincipal)) {
			throw new IllegalStateException("인증된 사용자가 아닙니다.");
		}

		UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
		return principal.getUsername(); // UserPrincipal에서 getUsername()은 이메일을 반환
	}

	/**
	 * 사용자가 인증되었는지 확인합니다.
	 *
	 * @return 인증된 경우 true, 그렇지 않은 경우 false
	 */
	public static boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated() &&
			authentication.getPrincipal() instanceof UserPrincipal;
	}
}