package sevenstar.marineleisure.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.jwt.JwtTokenProvider;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.AuthCodeRequest;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;
import sevenstar.marineleisure.member.dto.LoginResponse;
import sevenstar.marineleisure.member.service.OauthService;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@PropertySource("classpath:application-auth.properties")
public class AuthController {

    private final OauthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient;


    @Value("${kakao.login.api_key}")
    private String apiKey;

    @Value("${kakao.login.client_secret}")
    private String clientSecret;

    @Value("${kakao.login.uri.base}")
    private String kakaoBaseUri;

    @Value("${kakao.login.redirect_uri}")
    private String redirectUri;

    /**
     * 카카오 로그인 Url 생성
     *
     * @param redirectUri
     * @return
     */
    @GetMapping("/kakao/url")
    public ResponseEntity<BaseResponse<Map<String, String>>> getKakaoLoginUrl(
            @RequestParam(required = false) String redirectUri
    ) {
        String state = UUID.randomUUID().toString();

        // Use the provided redirectUri or fall back to the configured one
        String finalRedirectUri = redirectUri != null ? redirectUri : this.redirectUri;

        String kakaoAuthUrl = UriComponentsBuilder.fromUriString(kakaoBaseUri)
                .path("/oauth/authorize")
                .queryParam("client_id", apiKey)
                .queryParam("redirect_uri", finalRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build()
                .toUriString();
        return BaseResponse.success(Map.of("kakaoAuthUrl", kakaoAuthUrl, "state", state));
    }

    // 1. 카카오 인증 처리. 프론트에서 받아온 코드로 카카오에 Access 토큰을 요청한다.

    // GET 방식으로 code를 쿼리 파라미터로 받는 경우
    @GetMapping("/kakao/code")
    public ResponseEntity<BaseResponse<LoginResponse>> kakaoLoginGet(@RequestParam String code, HttpServletResponse response) {
        return processKakaoLogin(code, response);
    }

    // POST 방식으로 code를 요청 바디로 받는 경우
    @PostMapping("/kakao/code")
    public ResponseEntity<BaseResponse<LoginResponse>> kakaoLogin(@RequestBody AuthCodeRequest request, HttpServletResponse response) {
        return processKakaoLogin(request.code(), response);
    }

    // 카카오 로그인 처리 공통 로직
    private ResponseEntity<BaseResponse<LoginResponse>> processKakaoLogin(String code, HttpServletResponse response) {
        String tokenUrl = UriComponentsBuilder.fromUriString(kakaoBaseUri)
                .path("/oauth/token")
                .build()
                .toUriString();

        log.info("Exchanging authorization code for token with redirect URI: {}", redirectUri);
        log.info("Authorization code: {}", code);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", apiKey);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        KakaoTokenResponse tokenResponse = webClient.post()
                .uri(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        // 2. (1)에서 받아온 토큰으로 사용자 정보를 요청하고 처리한다
        Map<String, Object> userInfo = oauthService.processKakaoUser(tokenResponse != null ? tokenResponse.accessToken() : null);

        // 3. (2)에서 받아온 사용자 정보로 JWT 토큰 생성
        Member member = oauthService.findUserById((Long) userInfo.get("id"));
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);

        // 4. refresh 토큰 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshTokenCookie);

        // 5. 응답 생성
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .email(member.getEmail())
                .userId(member.getId())
                .nickname(member.getNickname())
                .build();
        return BaseResponse.success(loginResponse);
    }

}
