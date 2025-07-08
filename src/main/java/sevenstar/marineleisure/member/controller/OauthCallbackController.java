package sevenstar.marineleisure.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.member.dto.AuthCodeRequest;
import sevenstar.marineleisure.member.dto.LoginResponse;

/**
 * OAuth 제공자(kakao)에 등록된 callback 경로에서 호출되는 요청을 처리하는 컨트롤러
 * 실제 처리는 메인 AuthController에 위임
 */
@Slf4j
@RestController
public class OauthCallbackController {

    private final AuthController authController;

    public OauthCallbackController(AuthController authController) {
        this.authController = authController;
    }


    @GetMapping("/oauth/kakao/code")
    public ResponseEntity<BaseResponse<LoginResponse>> kakaoCallbackGet(
            @RequestParam String code,
            HttpServletResponse response) {
        log.info("Received Kakao OAuth callback (GET) at /oauth/kakao/code with code: {}", code);
        return authController.kakaoLoginGet(code, response);
    }


    @PostMapping("/oauth/kakao/code")
    public ResponseEntity<BaseResponse<LoginResponse>> kakaoCallbackPost(
            @RequestBody AuthCodeRequest request,
            HttpServletResponse response) {
        log.info("Received Kakao OAuth callback (POST) at /oauth/kakao/code with code: {}", request.code());
        return authController.kakaoLogin(request, response);
    }
}
