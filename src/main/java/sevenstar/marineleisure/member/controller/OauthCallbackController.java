package sevenstar.marineleisure.member.controller;

import jakarta.servlet.http.HttpServletResponse;
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

/**
 * OAuth 제공자(kakao)에 등록된 callback 경로에서 호출되는 요청을 처리하는 컨트롤러
 * 실제 처리는 메인 AuthController에 위임
 */
@Slf4j
@Controller
public class OauthCallbackController {

    private final AuthController authController;

    public OauthCallbackController(AuthController authController) {
        this.authController = authController;
    }


    @GetMapping("/oauth/kakao/code")
    public String kakaoCallbackGet() {
        log.info("Forwarding /oauth/kakao/code GET to index.html for client-side handling");
        // src/main/resources/static/index.html (또는 templates/index.html)이 보여지도록 포워드

        // react로 리다이렉트
        //return "redirect:" + clientAppUrl + "/oauth/kakao/callback?code=" + code + "&state=" + state;

        // 테스트를 위한 index.html로 리다이렉트
        return "forward:/index.html";
    }



    @PostMapping("/oauth/kakao/code")
    @ResponseBody
    public ResponseEntity<BaseResponse<LoginResponse>> kakaoCallbackPost(
            @RequestBody AuthCodeRequest request,
            HttpServletResponse response) {
        log.info("Received Kakao OAuth callback (POST) at /oauth/kakao/code with code: {}", request.code());
        return authController.kakaoLogin(request, response);
    }
}
