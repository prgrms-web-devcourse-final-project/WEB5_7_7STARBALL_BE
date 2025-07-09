package sevenstar.marineleisure.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import sevenstar.marineleisure.global.domain.BaseResponse;
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
     * 카카오 OAuth 콜백 처리 (GET)
     * 브라우저 리다이렉트를 통해 호출됨
     *
     * @return 클라이언트 페이지로 포워드
     */
    @GetMapping("/oauth/kakao/code")
    public String kakaoCallbackGet() {
        log.info("Forwarding /oauth/kakao/code GET to index.html for client-side handling");
        // src/main/resources/static/index.html (또는 templates/index.html)이 보여지도록 포워드

        // react로 리다이렉트
        //return "redirect:" + clientAppUrl + "/oauth/kakao/callback?code=" + code + "&state=" + state;
        return "forward:/index.html";
    }

    /**
     * 카카오 OAuth 콜백 처리 (POST)
     * 클라이언트에서 인증 코드를 받아 처리
     *
     * @param request 인증 코드 요청 DTO
     * @param response HTTP 응답
     * @return 로그인 응답 DTO
     */
    @PostMapping("/oauth/kakao/code")
    @ResponseBody
    public ResponseEntity<BaseResponse<LoginResponse>> kakaoCallbackPost(
            @RequestBody AuthCodeRequest request,
            HttpServletResponse response) {
        log.info("Received Kakao OAuth callback (POST) at /oauth/kakao/code with code: {}", request.code());
        try {
            LoginResponse loginResponse = authService.processKakaoLogin(request.code(), response);
            return BaseResponse.success(loginResponse);
        } catch (Exception e) {
            log.error("Kakao login failed: {}", e.getMessage(), e);
            return BaseResponse.error(500, 500, "카카오 로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}