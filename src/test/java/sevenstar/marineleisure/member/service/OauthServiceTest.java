package sevenstar.marineleisure.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;
import sevenstar.marineleisure.global.util.PkceUtil;
import sevenstar.marineleisure.global.util.StateEncryptionUtil;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;
import sevenstar.marineleisure.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class OauthServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private WebClient webClient;

	@Mock
	private StateEncryptionUtil stateEncryptionUtil;

	@Mock
	private PkceUtil pkceUtil;

	@InjectMocks
	private OauthService oauthService;

	@BeforeEach
	void setUp() {
		// 필요한 프로퍼티 설정
		ReflectionTestUtils.setField(oauthService, "apiKey", "test-api-key");
		ReflectionTestUtils.setField(oauthService, "clientSecret", "test-client-secret");
		ReflectionTestUtils.setField(oauthService, "kakaoBaseUri", "https://kauth.kakao.com");
		ReflectionTestUtils.setField(oauthService, "redirectUri", "http://localhost:8080/oauth/kakao/code");

		// StateEncryptionUtil 모킹 (lenient 설정으로 불필요한 stubbing 경고 방지)
		lenient().when(stateEncryptionUtil.encryptState(anyString())).thenReturn("encrypted-state");
		lenient().when(stateEncryptionUtil.validateState(anyString(), anyString())).thenReturn(true);

		// PkceUtil 모킹
		lenient().when(pkceUtil.generateCodeVerifier()).thenReturn("test-code-verifier");
		lenient().when(pkceUtil.generateCodeChallenge(anyString())).thenReturn("test-code-challenge");
	}

	@Test
	@DisplayName("카카오 로그인 URL을 생성할 수 있다")
	void getKakaoLoginUrl() {
		// when
		Map<String, String> result = oauthService.getKakaoLoginUrl(null, "test-code-challenge");

		// then
		assertThat(result).containsKey("kakaoAuthUrl");
		assertThat(result).containsKey("state");
		assertThat(result).containsKey("encryptedState");
		assertThat(result.get("encryptedState")).isEqualTo("encrypted-state");
		assertThat(result.get("kakaoAuthUrl")).contains("https://kauth.kakao.com/oauth/authorize");
		assertThat(result.get("kakaoAuthUrl")).contains("client_id=test-api-key");
		assertThat(result.get("kakaoAuthUrl")).contains("redirect_uri=http://localhost:8080/oauth/kakao/code");
		assertThat(result.get("kakaoAuthUrl")).contains("response_type=code");
		assertThat(result.get("kakaoAuthUrl")).contains("state=" + result.get("state"));
		assertThat(result.get("kakaoAuthUrl")).contains("code_challenge=test-code-challenge");
		assertThat(result.get("kakaoAuthUrl")).contains("code_challenge_method=S256");
	}

	@Test
	@DisplayName("커스텀 리다이렉트 URI로 카카오 로그인 URL을 생성할 수 있다")
	void getKakaoLoginUrlWithCustomRedirectUri() {
		// given
		String customRedirectUri = "http://custom-redirect.com/callback";

		// when
		Map<String, String> result = oauthService.getKakaoLoginUrl(customRedirectUri, "test-code-challenge");

		// then
		assertThat(result).containsKey("kakaoAuthUrl");
		assertThat(result).containsKey("state");
		assertThat(result).containsKey("encryptedState");
		assertThat(result.get("encryptedState")).isEqualTo("encrypted-state");
		assertThat(result.get("kakaoAuthUrl")).contains("redirect_uri=" + customRedirectUri);
		assertThat(result.get("kakaoAuthUrl")).contains("code_challenge=test-code-challenge");
		assertThat(result.get("kakaoAuthUrl")).contains("code_challenge_method=S256");
	}

	@Test
	@DisplayName("HttpServletRequest와 함께 카카오 로그인 URL을 생성할 수 있다")
	void getKakaoLoginUrlWithHttpServletRequest() {
		// given
		String customRedirectUri = "http://custom-redirect.com/callback";
		String codeChallenge = "test-code-challenge";
		HttpServletRequest request = mock(HttpServletRequest.class);

		// when
		Map<String, String> result = oauthService.getKakaoLoginUrl(customRedirectUri, codeChallenge, request);

		// then
		assertThat(result).containsKey("kakaoAuthUrl");
		assertThat(result).containsKey("state");
		assertThat(result).containsKey("encryptedState");
		assertThat(result.get("encryptedState")).isEqualTo("encrypted-state");
		assertThat(result.get("kakaoAuthUrl")).contains("redirect_uri=" + customRedirectUri);
		assertThat(result.get("kakaoAuthUrl")).contains("code_challenge=" + codeChallenge);
		assertThat(result.get("kakaoAuthUrl")).contains("code_challenge_method=S256");
	}

	@Test
	@DisplayName("인증 코드로 카카오 토큰을 교환할 수 있다")
	void exchangeCodeForToken() {
		// given
		String code = "test-auth-code";
		String codeVerifier = "test-code-verifier";
		String redirectUri = "http://localhost:8080/oauth/kakao/code";
		KakaoTokenResponse expectedResponse = KakaoTokenResponse.builder()
			.accessToken("test-access-token")
			.tokenType("bearer")
			.refreshToken("test-refresh-token")
			.expiresIn(3600L)
			.scope("profile")
			.refreshTokenExpiresIn(86400L)
			.build();

		// WebClient 모킹
		WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
		WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
		when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
		when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(KakaoTokenResponse.class)).thenReturn(Mono.just(expectedResponse));

		// when
		KakaoTokenResponse result = oauthService.exchangeCodeForToken(code, codeVerifier, redirectUri);

		// then
		assertThat(result).isNotNull();
		assertThat(result.accessToken()).isEqualTo("test-access-token");
		assertThat(result.refreshToken()).isEqualTo("test-refresh-token");
	}

	@Test
	@DisplayName("카카오 사용자 정보를 처리하고 회원 정보를 반환할 수 있다")
	void processKakaoUser() {
		// given
		String accessToken = "test-access-token";
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("id", 12345L);

		Map<String, Object> kakaoAccount = new HashMap<>();
		Map<String, Object> profile = new HashMap<>();
		profile.put("nickname", "testUser");
		kakaoAccount.put("profile", profile);
		kakaoAccount.put("email", "test@example.com");
		userInfo.put("kakao_account", kakaoAccount);

		Member member = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.build();

		// ID 설정 (리플렉션 사용)
		ReflectionTestUtils.setField(member, "id", 1L);

		// WebClient 모킹 - 간소화된 방식
		WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(userInfo));

		// MemberRepository 모킹
		when(memberRepository.findByProviderAndProviderId(eq("kakao"), eq("12345")))
			.thenReturn(Optional.empty());
		when(memberRepository.save(any(Member.class))).thenReturn(member);

		// when
		Member result = oauthService.processKakaoUser(accessToken);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getEmail()).isEqualTo("test@example.com");
		assertThat(result.getNickname()).isEqualTo("testUser");
	}

	@Test
	@DisplayName("기존 회원이 있는 경우 닉네임을 업데이트하고 회원 정보를 반환할 수 있다")
	void processKakaoUserWithExistingMember() {
		// given
		String accessToken = "test-access-token";
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("id", 12345L);

		Map<String, Object> kakaoAccount = new HashMap<>();
		Map<String, Object> profile = new HashMap<>();
		profile.put("nickname", "newNickname");
		kakaoAccount.put("profile", profile);
		kakaoAccount.put("email", "test@example.com");
		userInfo.put("kakao_account", kakaoAccount);

		Member existingMember = Member.builder()
			.nickname("oldNickname")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.build();

		// ID 설정 (리플렉션 사용)
		ReflectionTestUtils.setField(existingMember, "id", 1L);

		existingMember.updateNickname("newNickname");
		Member updatedMember = existingMember;

		// WebClient 모킹 - 간소화된 방식
		WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(userInfo));

		// MemberRepository 모킹
		when(memberRepository.findByProviderAndProviderId(eq("kakao"), eq("12345")))
			.thenReturn(Optional.of(existingMember));
		when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

		// when
		Member result = oauthService.processKakaoUser(accessToken);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getEmail()).isEqualTo("test@example.com");
		assertThat(result.getNickname()).isEqualTo("newNickname");
	}

	@Test
	@DisplayName("ID로 회원을 찾을 수 있다")
	void findUserById() {
		// given
		Long memberId = 1L;
		Member member = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.build();

		// ID 설정 (리플렉션 사용)
		ReflectionTestUtils.setField(member, "id", memberId);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

		// when
		Member result = oauthService.findUserById(memberId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(memberId);
		assertThat(result.getNickname()).isEqualTo("testUser");
		assertThat(result.getEmail()).isEqualTo("test@example.com");

		// verify
		verify(memberRepository).findById(memberId);
	}

	@Test
	@DisplayName("존재하지 않는 ID로 회원을 찾으면 예외가 발생한다")
	void findUserByIdNotFound() {
		// given
		Long memberId = 999L;
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> oauthService.findUserById(memberId))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessageContaining("User not found for id: " + memberId);

		// verify
		verify(memberRepository).findById(memberId);
	}

	@Test
	@DisplayName("카카오 계정 연결 끊기를 요청할 수 있다")
	void unlinkKakaoAccount() {
		// given
		String providerId = "12345";
		Map<String, Object> response = new HashMap<>();
		response.put("id", 12345L);

		// WebClient 모킹
		WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
		WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(eq("https://kapi.kakao.com/v1/user/unlink"))).thenReturn(requestBodySpec);
		when(requestBodySpec.header(eq("Authorization"), eq("KakaoAK test-client-secret"))).thenReturn(requestBodySpec);
		when(requestBodySpec.header(eq("Content-Type"), eq("application/x-www-form-urlencoded;charset=utf-8"))).thenReturn(requestBodySpec);
		when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

		// when
		Long result = oauthService.unlinkKakaoAccount(providerId);

		// then
		assertThat(result).isEqualTo(12345L);

		// verify
		verify(webClient).post();
	}

	@Test
	@DisplayName("카카오 계정 연결 끊기 실패 시 예외가 발생한다")
	void unlinkKakaoAccountFailed() {
		// given
		String providerId = "12345";
		Map<String, Object> response = new HashMap<>();
		// id 필드가 없는 응답

		// WebClient 모킹
		WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
		WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
		when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
		when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

		// when & then
		assertThatThrownBy(() -> oauthService.unlinkKakaoAccount(providerId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to unlink Kakao account");

		// verify
		verify(webClient).post();
	}

	@Test
	@DisplayName("상태값으로 리다이렉트 URI를 가져오고 캐시에서 제거할 수 있다")
	void consumeRedirectUri() {
		// given
		String state = "test-state";
		String expectedRedirectUri = "http://custom-redirect.com/callback";

		// 리플렉션을 사용하여 캐시에 직접 값 설정
		Cache<String, String> redirectUriCache = Caffeine.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.build();
		redirectUriCache.put(state, expectedRedirectUri);
		ReflectionTestUtils.setField(oauthService, "redirectUriCache", redirectUriCache);

		// when
		String result = oauthService.consumeRedirectUri(state);

		// then
		assertThat(result).isEqualTo(expectedRedirectUri);

		// 캐시에서 제거되었는지 확인
		assertThat(redirectUriCache.getIfPresent(state)).isNull();
	}
}
