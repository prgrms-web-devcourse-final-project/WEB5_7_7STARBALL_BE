package sevenstar.marineleisure.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@PropertySource("classpath:application-auth.properties")
//@CrossOrigin(origins = "https://your-react-app.com", allowCredentials = "true")
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
        refreshTokenCookie.setMaxAge((int) (14 * 24 * 60 * 60)); // 14일
        refreshTokenCookie.setAttribute("SameSite", "None");
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

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refreshToken(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) {
        log.info("Refreshing token with refresh token: {}", refreshToken);

        // 리프레시 토큰이 없는 경우
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.error("Empty refresh token: {}", refreshToken);
            return BaseResponse.error(401, 401, "리프레시 토큰이 없습니다.");
        }
        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.info("Invalid refresh token: {}", refreshToken);
            return BaseResponse.error(401, 401, "유효하지 않은 리프레시 토큰입니다.");
        }

        try {
            // 토큰에서 사용자 id 추출
            Long memberId = jwtTokenProvider.getMemberId(refreshToken);
            log.info("Refreshing token for userId: {}", memberId);
            Member member = oauthService.findUserById(memberId);

            // 기존 리프레시 토큰 블랙리스트에 추가
            jwtTokenProvider.blacklistRefreshToken(refreshToken);

            // new 토큰 발급
            String newAccessToken = jwtTokenProvider.createAccessToken(member);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(member);

            // new 리프레시 토큰 쿠키 저장
            Cookie refreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (14 * 24 * 60 * 60)); // 14일
            refreshTokenCookie.setAttribute("SameSite", "None");
            response.addCookie(refreshTokenCookie);

            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .email(member.getEmail())
                    .userId(member.getId())
                    .nickname(member.getNickname())
                    .build();

            log.info("토큰 재발급 성공: userId={}", memberId);
            return BaseResponse.success(loginResponse);
        } catch (Exception e) {
            log.error("토큰 재발급 중 오류 발생: {}", e.getMessage());
            return BaseResponse.error(500, 500, "토큰 재발급 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) {
        log.info("Logging out with refresh token: {}", refreshToken);

        // 리프레시 토큰이 있다면 블랙리스트에 추가
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                jwtTokenProvider.blacklistRefreshToken(refreshToken);
                log.info("리프레시 토큰 블랙리스트 추가 성공");
            } catch (Exception e) {
                log.error("리프레시 토큰 블랙리스트 추가 실패: {}", e.getMessage());
            }
        }
        // 리프레시 토큰 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refresh_token", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 쿠키 즉시 만료
        refreshTokenCookie.setAttribute("SameSite", "None");
        response.addCookie(refreshTokenCookie);

        log.info("로그아웃 성공");
        return BaseResponse.success(null);
    }
}
