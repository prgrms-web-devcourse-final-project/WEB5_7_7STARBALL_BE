package sevenstar.marineleisure.member.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.global.jwt.JwtTokenProvider;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.AuthCodeRequest;
import sevenstar.marineleisure.member.dto.LoginResponse;
import sevenstar.marineleisure.member.service.AuthService;
import sevenstar.marineleisure.member.service.OauthService;

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
	private final JwtTokenProvider jwtTokenProvider;

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
	 * 카카오 로그인 처리 (stateless)
	 *
	 * @param request 인증 코드 요청 DTO
	 * @param response HTTP 응답
	 * @return 로그인 응답 DTO
	 */
	@PostMapping("/kakao/code")
	public ResponseEntity<BaseResponse<LoginResponse>> kakaoLogin(
		@RequestBody AuthCodeRequest request,
		HttpServletResponse response
	) {
		log.info("Processing Kakao login with code: {}, state: {}, encryptedState: {}, error: {}, errorDescription: {}",
			request.code(), request.state(), request.encryptedState(), request.error(), request.errorDescription());

		// 에러 파라미터가 있는 경우 (사용자가 취소하거나 다른 에러가 발생한 경우)
		if (request.error() != null && !request.error().isEmpty()) {
			log.error("Kakao login error: {}, description: {}", request.error(), request.errorDescription());

			// 사용자가 취소한 경우 (error=access_denied)
			if ("access_denied".equals(request.error())) {
				return BaseResponse.error(MemberErrorCode.KAKAO_LOGIN_CANCELED);
			} else {
				// 다른 에러인 경우
				return BaseResponse.error(MemberErrorCode.KAKAO_LOGIN_ERROR);
			}
		}

		try {
			LoginResponse loginResponse = authService.processKakaoLogin(
				request.code(),
				request.state(),
				request.encryptedState(),
				request.codeVerifier(),
				response
			);
			return BaseResponse.success(loginResponse);
		} catch (AuthenticationException e) {
			log.error("Authentication failed: {}", e.getMessage(), e);
			return BaseResponse.error(MemberErrorCode.SECURITY_VALIDATION_FAILED);
		} catch (Exception e) {
			log.error("Kakao login failed: {}", e.getMessage(), e);
			return BaseResponse.error(MemberErrorCode.KAKAO_LOGIN_ERROR);
		}
	}

	/**
	 * 토큰 재발급
	 *
	 * @param refreshToken 리프레시 토큰 (쿠키 또는 요청 본문에서 추출)
	 * @param refreshTokenFromBody 요청 본문에서 전달된 리프레시 토큰 (jwt.use-cookie=false 설정용)
	 * @param response HTTP 응답
	 * @return 새로운 액세스 토큰과 사용자 정보
	 */
	@PostMapping("/refresh")
	public ResponseEntity<BaseResponse<LoginResponse>> refreshToken(
		@CookieValue(value = "refresh_token", required = false) String refreshToken,
		@RequestBody(required = false) Map<String, String> refreshTokenFromBody,
		HttpServletResponse response
	) {
		log.info("Refreshing token");

		try {
			String token = refreshToken;

			// jwt.use-cookie=false 설정일 때는 요청 본문에서 리프레시 토큰 추출
			if ((token == null || token.isEmpty()) && refreshTokenFromBody != null) {
				token = refreshTokenFromBody.get("refreshToken");
				log.info("Using refresh token from request body: {}", token);
			}

			// 리프레시 토큰이 없는 경우
			if (token == null || token.isEmpty()) {
				log.error("Empty refresh token");
				return BaseResponse.error(MemberErrorCode.REFRESH_TOKEN_MISSING);
			}

			LoginResponse loginResponse = authService.refreshToken(token, response);
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


	/**
	 * 테스트용 JWT 액세스 토큰 생성
	 * 카카오 웹사이트에서 직접 발급받은 액세스 토큰으로 JWT 토큰 생성
	 * 
	 * @param kakaoAccessToken 카카오 액세스 토큰
	 * @return JWT 액세스 토큰과 사용자 정보
	 */
	@PostMapping("/kakao/test-jwt")
	public ResponseEntity<BaseResponse<LoginResponse>> createTestJwtToken(
		@RequestParam String kakaoAccessToken
	) {
		log.info("Creating test JWT token with Kakao access token");

		try {
			// 카카오 액세스 토큰으로 사용자 정보 조회 및 Member 객체 생성/조회
			Member member = oauthService.processKakaoUser(kakaoAccessToken);

			// JWT 액세스 토큰 생성
			String jwtAccessToken = jwtTokenProvider.createAccessToken(member);

			// 로그인 응답 생성
			LoginResponse loginResponse = LoginResponse.builder()
				.accessToken(jwtAccessToken)
				.email(member.getEmail())
				.userId(member.getId())
				.nickname(member.getNickname())
				.build();

			return BaseResponse.success(loginResponse);
		} catch (Exception e) {
			log.error("Failed to create test JWT token: {}", e.getMessage(), e);
			return BaseResponse.error(MemberErrorCode.KAKAO_LOGIN_ERROR);
		}
	}
}
