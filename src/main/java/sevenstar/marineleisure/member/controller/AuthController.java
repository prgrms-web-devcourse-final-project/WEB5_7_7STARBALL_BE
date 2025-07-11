package sevenstar.marineleisure.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.member.dto.AuthCodeRequest;
import sevenstar.marineleisure.member.dto.LoginResponse;
import sevenstar.marineleisure.member.service.AuthService;
import sevenstar.marineleisure.member.service.OauthService;

import java.io.IOException;
import java.util.Map;

/**
 * 인증 관련 요청을 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final OauthService oauthService;
	private final AuthService authService;

	/**
	 * GET /auth/kakao?redirectUri=…
	 * → 내부에서 state도 생성해서 저장하고,
	 *    kakaAuthUrl 로 곧장 302 리다이렉트
	 */
	@GetMapping("/kakao")
	public void kakaoLoginRedirect(
		@RequestParam String redirectUri,
		HttpServletRequest request,
		HttpServletResponse resp
	) throws IOException {
		Map<String, String> info = oauthService.getKakaoLoginUrl(redirectUri, request);
		// state는 이제 세션에 저장됨
		resp.sendRedirect(info.get("kakaoAuthUrl"));
	}

	/**
	 * 카카오 로그인 URL 생성
	 *
	 * @param redirectUri 커스텀 리다이렉트 URI (선택적)
	 * @return 카카오 로그인 URL과 state 값을 포함한 응답
	 */
	@GetMapping("/kakao/url")
	public ResponseEntity<BaseResponse<Map<String, String>>> getKakaoLoginUrl(
		@RequestParam(required = false) String redirectUri,
		HttpServletRequest request
	) {
		log.info("Generating Kakao login URL with redirectUri: {}", redirectUri);
		Map<String, String> loginUrlInfo = oauthService.getKakaoLoginUrl(redirectUri, request);
		return BaseResponse.success(loginUrlInfo);
	}

	/**
	 * 카카오 로그인 처리
	 *
	 * @param request 인증 코드 요청 DTO
	 * @param httpRequest HTTP 요청
	 * @param response HTTP 응답
	 * @return 로그인 응답 DTO
	 */
	@PostMapping("/kakao/code")
	public ResponseEntity<BaseResponse<LoginResponse>> kakaoLogin(
		@RequestBody AuthCodeRequest request,
		HttpServletRequest httpRequest,
		HttpServletResponse response
	) {
		log.info("Processing Kakao login with code: {}, state: {}", request.code(), request.state());
		try {
			LoginResponse loginResponse = authService.processKakaoLogin(request.code(), request.state(), httpRequest,
				response);
			return BaseResponse.success(loginResponse);
		} catch (SecurityException e) {
			log.error("Security validation failed: {}", e.getMessage(), e);
			return BaseResponse.error(MemberErrorCode.SECURITY_VALIDATION_FAILED);
		} catch (Exception e) {
			log.error("Kakao login failed: {}", e.getMessage(), e);
			return BaseResponse.error(MemberErrorCode.KAKAO_LOGIN_ERROR);
		}
	}

	/**
	 * 토큰 재발급
	 *
	 * @param refreshToken 리프레시 토큰 (쿠키에서 추출)
	 * @param response HTTP 응답
	 * @return 새로운 액세스 토큰과 사용자 정보
	 */
	@PostMapping("/refresh")
	public ResponseEntity<BaseResponse<LoginResponse>> refreshToken(
		@CookieValue(value = "refresh_token",required = false) String refreshToken,
		HttpServletResponse response
	) {
		log.info("Refreshing token with refresh token: {}", refreshToken);

		try {
			// 리프레시 토큰이 없는 경우
			if (refreshToken == null || refreshToken.isEmpty()) {
				log.error("Empty refresh token");
				return BaseResponse.error(MemberErrorCode.REFRESH_TOKEN_MISSING);
			}

			LoginResponse loginResponse = authService.refreshToken(refreshToken, response);
			return BaseResponse.success(loginResponse);
		} catch (IllegalArgumentException e) {
			log.info("Invalid refresh token: {}", e.getMessage());
			return BaseResponse.error(MemberErrorCode.REFRESH_TOKEN_INVALID);
		} catch (Exception e) {
			log.error("Token refresh failed: {}", e.getMessage(), e);
			return BaseResponse.error(MemberErrorCode.TOKEN_REFRESH_ERROR);
		}
	}

	/**
	 * 로그아웃
	 *
	 * @param refreshToken 리프레시 토큰 (쿠키에서 추출)
	 * @param response HTTP 응답
	 * @return 성공 응답
	 */
	@PostMapping("/logout")
	public ResponseEntity<BaseResponse<String>> logout(
		@CookieValue(value = "refresh_token", required = false) String refreshToken,
		HttpServletResponse response
	) {
		log.info("Logging out with refresh token: {}", refreshToken);

		try {
			authService.logout(refreshToken, response);
			return BaseResponse.success(null);
		} catch (Exception e) {
			log.error("Logout failed: {}", e.getMessage(), e);
			return BaseResponse.error(MemberErrorCode.LOGOUT_ERROR);
		}
	}
}
