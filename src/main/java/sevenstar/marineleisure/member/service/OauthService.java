package sevenstar.marineleisure.member.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.util.PkceUtil;
import sevenstar.marineleisure.global.util.StateEncryptionUtil;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;
import sevenstar.marineleisure.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

	private final MemberRepository memberRepository;
	private final WebClient webClient;
	private final StateEncryptionUtil stateEncryptionUtil;
	private final PkceUtil pkceUtil;

	@Value("${kakao.login.api_key}")
	private String apiKey;

	@Value("${kakao.login.client_secret}")
	private String clientSecret;

	@Value("${kakao.login.uri.base}")
	private String kakaoBaseUri;

	@Value("${kakao.login.redirect_uri}")
	private String redirectUri;


	/**
	 * 카카오 로그인 URL 생성 (stateless)
	 *
	 * @param customRedirectUri 커스텀 리다이렉트 URI (null인 경우 기본값 사용)
	 * @return 카카오 로그인 URL, state 값, 암호화된 state 값을 포함한 Map
	 */
	public Map<String, String> getKakaoLoginUrl(String customRedirectUri, String codeChallenge) {

		String state = UUID.randomUUID().toString();

		/// 기존 서버에서 codeVerifier 생성하는 코드 흐름
		// String codeVerifier = pkceUtil.generateCodeVerifier();
		// String codeChallenge = pkceUtil.generateCodeChallenge(codeVerifier);

		String encryptedState = stateEncryptionUtil.encryptState(state);

		log.info("Generated OAuth state: {} (encrypted: {})", state, encryptedState);
		// log.info("Generated PKCE code_verifier: {} (challenge: {})", codeVerifier, codeChallenge);

		// Use the provided redirectUri or fall back to the configured one
		String finalRedirectUri = customRedirectUri != null ? customRedirectUri : this.redirectUri;

		String kakaoAuthUrl = UriComponentsBuilder.fromUriString(kakaoBaseUri)
			.path("/oauth/authorize")
			.queryParam("client_id", apiKey)
			.queryParam("redirect_uri", finalRedirectUri)
			.queryParam("response_type", "code")
			.queryParam("state", state)
			.queryParam("code_challenge", codeChallenge)
			.queryParam("code_challenge_method", "S256")
			.build()
			.toUriString();

		return Map.of(
			"kakaoAuthUrl", kakaoAuthUrl,
			"state", state,
			"encryptedState", encryptedState
			// "codeVerifier", codeVerifier // 추가.
		);
	}

	/**
	 * 카카오 로그인 URL 생성 (stateless - HttpServletRequest 호환용)
	 *
	 * @param customRedirectUri 커스텀 리다이렉트 URI (null인 경우 기본값 사용)
	 * @param request HTTP 요청 (호환성을 위해 유지, 사용하지 않음)
	 * @return 카카오 로그인 URL, state 값, 암호화된 state 값을 포함한 Map
	 */
	public Map<String, String> getKakaoLoginUrl(String customRedirectUri,String codeChallenge ,HttpServletRequest request) {
		// 세션 사용하지 않고 stateless 방식으로 구현
		return getKakaoLoginUrl(customRedirectUri, codeChallenge);
	}

	/**
	 * 카카오 인증 코드로 토큰 교환
	 *
	 * @param code         인증 코드
	 * @param codeVerifier
	 * @return 카카오 토큰 응답
	 */
	public KakaoTokenResponse exchangeCodeForToken(String code, String codeVerifier) {
		String tokenUrl = UriComponentsBuilder.fromUriString(kakaoBaseUri)
			.path("/oauth/token")
			.build()
			.toUriString();

		log.info("Exchanging authorization code for token with redirect URI: {}", redirectUri);
		log.info("Authorization code: {}", code);
		log.info("PKCE code_verifier: {}", codeVerifier);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", apiKey);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);
		params.add("client_secret", clientSecret);
		params.add("code_verifier", codeVerifier);

		return webClient.post()
			.uri(tokenUrl)
			.header("Content-Type", "application/x-www-form-urlencoded")
			.body(BodyInserters.fromFormData(params))
			.retrieve()
			.bodyToMono(KakaoTokenResponse.class)
			.block();
	}

	@Transactional
	public Member processKakaoUser(String accessToken) {
		// 1. access token으로 사용자 정보 요청
		Map<String, Object> memberAttributes = getUserInfo(accessToken);
		// 2. 사용자 정보로 회원가입 or 로그인 처리
		return saveOrUpdateKakaoUser(memberAttributes);
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
		Long providerId = (Long)memberAttributes.get("id");
		Map<String, Object> kakaoAccount = (Map<String, Object>)memberAttributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");

		String email = (String)kakaoAccount.get("email");
		String nickname = (String)profile.get("nickname");

		// 기존 회원이 있으면 가져오고, 없으면 새로 생성 (Optional이 비어있을 때만 실행)
		Member member = memberRepository.findByProviderAndProviderId("kakao", String.valueOf(providerId))
			.orElseGet(() -> Member.builder()
				.provider("kakao")
				.providerId(String.valueOf(providerId))
				.email(email)  // 새 회원 생성 시 이메일 설정
				.nickname(nickname)  // 새 회원 생성 시 닉네임 설정
				.latitude(BigDecimal.ZERO)
				.longitude(BigDecimal.ZERO)
				.build());
		member.updateNickname(nickname);

		return memberRepository.save(member);
	}

	public Member findUserById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(
				() -> new NoSuchElementException("User not found for id: " + id + " or email: " + id + "@kakao.com"));
	}
}