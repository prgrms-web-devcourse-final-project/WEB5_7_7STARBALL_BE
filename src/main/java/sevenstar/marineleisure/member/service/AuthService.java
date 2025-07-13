package sevenstar.marineleisure.member.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.jwt.JwtTokenProvider;
import sevenstar.marineleisure.global.util.CookieUtil;
import sevenstar.marineleisure.global.util.StateEncryptionUtil;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;
import sevenstar.marineleisure.member.dto.LoginResponse;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final OauthService oauthService;
	private final CookieUtil cookieUtil;
	private final StateEncryptionUtil stateEncryptionUtil;

	/**
	 * 카카오 로그인 처리 (stateless)
	 *
	 * @param code 인증 코드
	 * @param state OAuth state 파라미터
	 * @param encryptedState 암호화된 state 값
	 * @param response HTTP 응답
	 * @return 로그인 응답 DTO
	 */
	public LoginResponse processKakaoLogin(String code, String state, String encryptedState, HttpServletResponse response) {
		// 0. state 검증 (stateless)
		log.info("Validating OAuth state: received={}, encrypted={}", state, encryptedState);

		if (!stateEncryptionUtil.validateState(state, encryptedState)) {
			log.error("State validation failed: possible CSRF attack");
			throw new SecurityException("Possible CSRF attack: state parameter doesn't match");
		}

		// 1. 인증 코드로 카카오 토큰 교환
		KakaoTokenResponse tokenResponse = oauthService.exchangeCodeForToken(code);

		// 2. 카카오 토큰으로 사용자 정보 요청 및 처리
		String accessToken = tokenResponse != null ? tokenResponse.accessToken() : null;
		if (accessToken == null) {
			log.error("Failed to get access token from Kakao");
			throw new RuntimeException("Failed to get access token from Kakao");
		}

		// 3. 사용자 정보 처리 및 회원 조회
		Member member = oauthService.processKakaoUser(accessToken);

		// 4. JWT 토큰 생성
		String jwtAccessToken = jwtTokenProvider.createAccessToken(member);
		String refreshToken = jwtTokenProvider.createRefreshToken(member);

		// 5. 리프레시 토큰 쿠키 설정
		cookieUtil.addCookie(response, cookieUtil.createRefreshTokenCookie(refreshToken));

		// 6. 로그인 응답 생성
		return createLoginResponse(member, jwtAccessToken);
	}

	// /**
	//  * 카카오 로그인 처리 (리팩토링 전 세션 기반)
	//  *
	//  * @param code 인증 코드
	//  * @param state OAuth state 파라미터
	//  * @param request HTTP 요청
	//  * @param response HTTP 응답
	//  * @return 로그인 응답 DTO
	//  * @deprecated 보안을 위해 {@link #processKakaoLogin(String, String, String, HttpServletResponse)} 사용
	//  */
	// @Deprecated
	// public LoginResponse processKakaoLogin(String code, String state, HttpServletRequest request,
	// 	HttpServletResponse response) {
	// 	log.warn("세션 기반 인증은 deprecated 되었습니다. stateless 인증을 사용하세요.");
	//
	// 	// 세션에서 state 검증 (이전 방식)
	// 	HttpSession session = request.getSession(false);
	// 	String storedState = session != null ? (String)session.getAttribute("oauth_state") : null;
	//
	// 	log.info("Validating OAuth state (session-based): received={}, stored={}", state, storedState);
	//
	// 	if (storedState == null || !storedState.equals(state)) {
	// 		log.error("State validation failed: possible CSRF attack");
	// 		throw new SecurityException("Possible CSRF attack: state parameter doesn't match");
	// 	}
	//
	// 	// 세션에서 state 제거 (일회용)
	// 	if (session != null) {
	// 		session.removeAttribute("oauth_state");
	// 	}
	//
	// 	// 나머지 로직은 stateless 메서드와 동일하므로 코드 중복을 피하기 위해 내부적으로 처리
	// 	// 실제 구현에서는 encryptedState가 없으므로 임시로 빈 문자열 전달
	// 	return processKakaoLogin(code, state, "", response);
	// }

	/**
	 * 토큰 재발급
	 *
	 * @param refreshToken 리프레시 토큰
	 * @param response HTTP 응답
	 * @return 로그인 응답 DTO
	 */
	public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) {
		// 1. 리프레시 토큰 검증
		if (refreshToken == null || refreshToken.isEmpty()) {
			log.error("Empty refresh token");
			throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
		}

		if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
			log.info("Invalid refresh token: {}", refreshToken);
			throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
		}

		// 2. 토큰에서 사용자 ID 추출 및 회원 조회
		Long memberId = jwtTokenProvider.getMemberId(refreshToken);
		log.info("Refreshing token for userId: {}", memberId);
		Member member = oauthService.findUserById(memberId);

		// 3. 기존 리프레시 토큰 블랙리스트에 추가
		jwtTokenProvider.blacklistRefreshToken(refreshToken);

		// 4. 새 토큰 발급
		String newAccessToken = jwtTokenProvider.createAccessToken(member);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(member);

		// 5. 새 리프레시 토큰 쿠키 설정
		cookieUtil.addCookie(response, cookieUtil.createRefreshTokenCookie(newRefreshToken));

		// 6. 로그인 응답 생성
		log.info("토큰 재발급 성공: userId={}", memberId);
		return createLoginResponse(member, newAccessToken);
	}

	/**
	 * 로그아웃
	 *
	 * @param refreshToken 리프레시 토큰
	 * @param response HTTP 응답
	 */
	public void logout(String refreshToken, HttpServletResponse response) {
		log.info("Logging out with refresh token: {}", refreshToken);

		// 1. 리프레시 토큰이 있다면 블랙리스트에 추가
		if (refreshToken != null && !refreshToken.isEmpty()) {
			try {
				jwtTokenProvider.blacklistRefreshToken(refreshToken);
				log.info("리프레시 토큰 블랙리스트 추가 성공");
			} catch (Exception e) {
				log.error("리프레시 토큰 블랙리스트 추가 실패: {}", e.getMessage());
			}
		}

		// 2. 리프레시 토큰 쿠키 삭제
		cookieUtil.addCookie(response, cookieUtil.deleteRefreshTokenCookie());

		log.info("로그아웃 성공");
	}

	/**
	 * 로그인 응답 DTO 생성
	 *
	 * @param member 회원 정보
	 * @param accessToken 액세스 토큰
	 * @return 로그인 응답 DTO
	 */
	private LoginResponse createLoginResponse(Member member, String accessToken) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.email(member.getEmail())
			.userId(member.getId())
			.nickname(member.getNickname())
			.build();
	}
}
