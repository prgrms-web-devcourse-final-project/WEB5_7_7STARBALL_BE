package sevenstar.marineleisure.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;
import sevenstar.marineleisure.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-auth.properties")
public class OauthService {

    private final MemberRepository memberRepository;
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
     * 카카오 로그인 URL 생성 (세션 저장 없음 - 테스트용)
     * 
     * @param customRedirectUri 커스텀 리다이렉트 URI (null인 경우 기본값 사용)
     * @return 카카오 로그인 URL과 state 값을 포함한 Map
     * @deprecated 보안을 위해 {@link #getKakaoLoginUrl(String, HttpServletRequest)} 사용
     */
    @Deprecated
    public Map<String, String> getKakaoLoginUrl(String customRedirectUri) {
        String state = UUID.randomUUID().toString();
        log.warn("deprecated 되었습니다. state 검증 없이 test코드 돌리기 위한 메서드");
        // Use the provided redirectUri or fall back to the configured one
        String finalRedirectUri = customRedirectUri != null ? customRedirectUri : this.redirectUri;

        String kakaoAuthUrl = UriComponentsBuilder.fromUriString(kakaoBaseUri)
                .path("/oauth/authorize")
                .queryParam("client_id", apiKey)
                .queryParam("redirect_uri", finalRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build()
                .toUriString();

        return Map.of("kakaoAuthUrl", kakaoAuthUrl, "state", state);
    }

    /**
     * 카카오 로그인 URL 생성 (세션에 state 저장)
     *
     * @param customRedirectUri 커스텀 리다이렉트 URI (null인 경우 기본값 사용)
     * @param request HTTP 요청 (세션에 state 저장용)
     * @return 카카오 로그인 URL과 state 값을 포함한 Map
     */
    public Map<String, String> getKakaoLoginUrl(String customRedirectUri, HttpServletRequest request) {
        String state = UUID.randomUUID().toString();

        // Store state in session for later verification
        HttpSession session = request.getSession();
        session.setAttribute("oauth_state", state);
        log.info("Stored OAuth state in session: {}", state);

        // Use the provided redirectUri or fall back to the configured one
        String finalRedirectUri = customRedirectUri != null ? customRedirectUri : this.redirectUri;

        String kakaoAuthUrl = UriComponentsBuilder.fromUriString(kakaoBaseUri)
                .path("/oauth/authorize")
                .queryParam("client_id", apiKey)
                .queryParam("redirect_uri", finalRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build()
                .toUriString();

        return Map.of("kakaoAuthUrl", kakaoAuthUrl, "state", state);
    }

    /**
     * 카카오 인증 코드로 토큰 교환
     *
     * @param code 인증 코드
     * @return 카카오 토큰 응답
     */
    public KakaoTokenResponse exchangeCodeForToken(String code) {
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

        return webClient.post()
                .uri(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    @Transactional
    public Map<String, Object> processKakaoUser(String accessToken) {
        // 1. access token으로 사용자 정보 요청
        Map<String, Object> memberAttributes = getUserInfo(accessToken);
        // 2. 사용자 정보로 회원가입 or 로그인 처리
        Member member = saveOrUpdateKakaoUser(memberAttributes);
        // 3. 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("id", member != null ? member.getId() : null);
        response.put("email", member != null ? member.getEmail() : null);
        response.put("nickname", member != null ? member.getNickname() : null);
        return response;
    }


    /**
     * 카카오 API로 사용자 정보 요청
     *
     * @param accessToken
     * @return
     */
    private Map<String, Object> getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    /**
     * 카카오 사용자 정보로 회원가입 or 로그인 처리
     *
     * @param memberAttributes
     * @return
     */
    private Member saveOrUpdateKakaoUser(Map<String, Object> memberAttributes) {
        Long id = (Long) memberAttributes.get("id");
        Map<String, Object> kakaoAccount = (Map<String, Object>) memberAttributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // 좌표 설정을 어떻게 하는가? update 시에 해줘야 할듯 한데.
        Member member = memberRepository.findByProviderAndProviderId("kakao", String.valueOf(id))
                .map(e -> e.update(nickname))
                .orElse(Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .provider("kakao")
                        .providerId(String.valueOf(id))
                        .latitude(BigDecimal.valueOf(0))
                        .longitude(BigDecimal.valueOf(0))
                        .build()
                );

        return memberRepository.save(member);
    }


    public Member findUserById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found for id: " + id + " or email: " + id + "@kakao.com"));
    }
}
