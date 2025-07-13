package sevenstar.marineleisure.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.member.dto.AuthCodeRequest;
import sevenstar.marineleisure.member.dto.LoginResponse;
import sevenstar.marineleisure.member.service.AuthService;

/**
 * OAuth 제공자(kakao)에 등록된 callback 경로에서 호출되는 요청을 처리하는 컨트롤러
 * 실제 처리는 메인 AuthService에 위임
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class OauthCallbackController {
	private final AuthService authService;

	/**
	 * 카카오 OAuth 콜백 처리 (POST)
	 * 클라이언트에서 인증 코드를 받아 처리
	 *
	 * @param request 인증 코드 요청 DTO
	 * @param httpRequest HTTP 요청 (세션 접근용)
	 * @param response HTTP 응답
	 * @return 로그인 응답 DTO
	 */
	@PostMapping("/oauth/kakao/code")
	@ResponseBody
	public ResponseEntity<BaseResponse<LoginResponse>> kakaoCallbackPost(
		@RequestBody AuthCodeRequest request,
		HttpServletRequest httpRequest,
		HttpServletResponse response) {
		log.info("Received Kakao OAuth callback (POST) at /oauth/kakao/code with code: {}, state: {}", request.code(),
			request.state());
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
}
