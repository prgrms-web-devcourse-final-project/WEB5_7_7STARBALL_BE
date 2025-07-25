package sevenstar.marineleisure.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import sevenstar.marineleisure.global.jwt.JwtTokenProvider;
import sevenstar.marineleisure.global.util.CookieUtil;
import sevenstar.marineleisure.global.util.StateEncryptionUtil;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;
import sevenstar.marineleisure.member.dto.LoginResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private OauthService oauthService;

	@Mock
	private CookieUtil cookieUtil;

	@Mock
	private StateEncryptionUtil stateEncryptionUtil;

	@InjectMocks
	private AuthService authService;

	private Member testMember;
	private HttpServletResponse mockResponse;
	private Cookie mockCookie;

 @BeforeEach
	void setUp() {
		// 테스트용 Member 객체 생성
		testMember = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.build();

		// ID 설정 (리플렉션 사용)
		ReflectionTestUtils.setField(testMember, "id", 1L);

		// Mock HttpServletResponse
		mockResponse = mock(HttpServletResponse.class);

		// Mock Cookie
		mockCookie = mock(Cookie.class);

		// useCookie 설정 (기본값: true)
		ReflectionTestUtils.setField(authService, "useCookie", true);
	}

	@Test
	@DisplayName("카카오 로그인을 처리하고 로그인 응답을 반환할 수 있다 (쿠키 모드)")
	void processKakaoLogin() {
		// given
		String code = "test-auth-code";
		String state = "test-state";
		String encryptedState = "encrypted-test-state";
		String accessToken = "kakao-access-token";
		String jwtAccessToken = "jwt-access-token";
		String refreshToken = "jwt-refresh-token";
		String codeVerifier = "test-code-verifier";

		// useCookie = true 설정 (기본값)
		ReflectionTestUtils.setField(authService, "useCookie", true);

		// 카카오 토큰 응답 설정
		KakaoTokenResponse tokenResponse = KakaoTokenResponse.builder()
			.accessToken(accessToken)
			.tokenType("bearer")
			.refreshToken("kakao-refresh-token")
			.expiresIn(3600L)
			.build();

		// 쿠키 설정
		when(cookieUtil.createRefreshTokenCookie(refreshToken)).thenReturn(mockCookie);

		// state 검증 모킹
		when(stateEncryptionUtil.validateState(state, encryptedState)).thenReturn(true);

		// 서비스 메서드 모킹
		String redirectUri = "http://localhost:8080/oauth/kakao/code";
		when(oauthService.exchangeCodeForToken(code, codeVerifier, redirectUri)).thenReturn(tokenResponse);
		when(oauthService.processKakaoUser(accessToken)).thenReturn(testMember);
		// findUserById는 이제 필요 없음 (processKakaoUser가 직접 Member를 반환)
		when(jwtTokenProvider.createAccessToken(testMember)).thenReturn(jwtAccessToken);
		when(jwtTokenProvider.createRefreshToken(testMember)).thenReturn(refreshToken);

		// when
		LoginResponse response = authService.processKakaoLogin(code, state, encryptedState, codeVerifier, mockResponse, redirectUri);

		// then
		assertThat(response).isNotNull();
		assertThat(response.accessToken()).isEqualTo(jwtAccessToken);
		assertThat(response.userId()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("test@example.com");
		assertThat(response.nickname()).isEqualTo("testUser");
		assertThat(response.refreshToken()).isNull(); // 쿠키 모드에서는 refreshToken이 응답에 포함되지 않음

		// 쿠키 추가 확인
		verify(cookieUtil).addCookie(mockResponse, mockCookie);
	}

	@Test
	@DisplayName("카카오 로그인을 처리하고 로그인 응답을 반환할 수 있다 (비쿠키 모드)")
	void processKakaoLogin_noCookie() {
		// given
		String code = "test-auth-code";
		String state = "test-state";
		String encryptedState = "encrypted-test-state";
		String accessToken = "kakao-access-token";
		String jwtAccessToken = "jwt-access-token";
		String refreshToken = "jwt-refresh-token";
		String codeVerifier = "test-code-verifier";

		// useCookie = false 설정
		ReflectionTestUtils.setField(authService, "useCookie", false);

		// 카카오 토큰 응답 설정
		KakaoTokenResponse tokenResponse = KakaoTokenResponse.builder()
			.accessToken(accessToken)
			.tokenType("bearer")
			.refreshToken("kakao-refresh-token")
			.expiresIn(3600L)
			.build();

		// state 검증 모킹
		when(stateEncryptionUtil.validateState(state, encryptedState)).thenReturn(true);

		// 서비스 메서드 모킹
		String redirectUri = "http://localhost:8080/oauth/kakao/code";
		when(oauthService.exchangeCodeForToken(code, codeVerifier, redirectUri)).thenReturn(tokenResponse);
		when(oauthService.processKakaoUser(accessToken)).thenReturn(testMember);
		when(jwtTokenProvider.createAccessToken(testMember)).thenReturn(jwtAccessToken);
		when(jwtTokenProvider.createRefreshToken(testMember)).thenReturn(refreshToken);

		// when
		LoginResponse response = authService.processKakaoLogin(code, state, encryptedState, codeVerifier, mockResponse, redirectUri);

		// then
		assertThat(response).isNotNull();
		assertThat(response.accessToken()).isEqualTo(jwtAccessToken);
		assertThat(response.userId()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("test@example.com");
		assertThat(response.nickname()).isEqualTo("testUser");
		assertThat(response.refreshToken()).isEqualTo(refreshToken); // 비쿠키 모드에서는 refreshToken이 응답에 포함됨

		// 쿠키 추가되지 않음 확인
		verify(cookieUtil, never()).addCookie(any(), any());
	}

	@Test
	@DisplayName("카카오 액세스 토큰이 없으면 예외가 발생한다")
	void processKakaoLogin_noAccessToken() {
		// given
		String code = "test-auth-code";
		String state = "test-state";
		String encryptedState = "encrypted-test-state";
		String codeVerifier = "test-code-verifier";

		// 액세스 토큰이 없는 응답 설정
		KakaoTokenResponse tokenResponse = KakaoTokenResponse.builder()
			.accessToken(null)
			.tokenType("bearer")
			.refreshToken("kakao-refresh-token")
			.expiresIn(3600L)
			.build();

		// state 검증 모킹
		when(stateEncryptionUtil.validateState(state, encryptedState)).thenReturn(true);

		String redirectUri = "http://localhost:8080/oauth/kakao/code";
		when(oauthService.exchangeCodeForToken(code, codeVerifier, redirectUri)).thenReturn(tokenResponse);

		// when & then
		assertThatThrownBy(() -> authService.processKakaoLogin(code, state, encryptedState, codeVerifier, mockResponse, redirectUri))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to get access token from Kakao");
	}

	@Test
	@DisplayName("리프레시 토큰으로 새 토큰을 발급할 수 있다 (쿠키 모드)")
	void refreshToken() {
		// given
		String refreshToken = "valid-refresh-token";
		String newAccessToken = "new-access-token";
		String newRefreshToken = "new-refresh-token";

		// useCookie = true 설정 (기본값)
		ReflectionTestUtils.setField(authService, "useCookie", true);

		// 쿠키 설정
		when(cookieUtil.createRefreshTokenCookie(newRefreshToken)).thenReturn(mockCookie);

		// 토큰 검증 및 생성 설정
		when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
		when(jwtTokenProvider.getMemberId(refreshToken)).thenReturn(1L);
		when(oauthService.findUserById(1L)).thenReturn(testMember);
		when(jwtTokenProvider.createAccessToken(testMember)).thenReturn(newAccessToken);
		when(jwtTokenProvider.createRefreshToken(testMember)).thenReturn(newRefreshToken);

		// when
		LoginResponse response = authService.refreshToken(refreshToken, mockResponse);

		// then
		assertThat(response).isNotNull();
		assertThat(response.accessToken()).isEqualTo(newAccessToken);
		assertThat(response.userId()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("test@example.com");
		assertThat(response.nickname()).isEqualTo("testUser");
		assertThat(response.refreshToken()).isNull(); // 쿠키 모드에서는 refreshToken이 응답에 포함되지 않음

		// 기존 토큰 블랙리스트 추가 확인
		verify(jwtTokenProvider).blacklistRefreshToken(refreshToken);

		// 새 쿠키 추가 확인
		verify(cookieUtil).addCookie(mockResponse, mockCookie);
	}

	@Test
	@DisplayName("리프레시 토큰으로 새 토큰을 발급할 수 있다 (비쿠키 모드)")
	void refreshToken_noCookie() {
		// given
		String refreshToken = "valid-refresh-token";
		String newAccessToken = "new-access-token";
		String newRefreshToken = "new-refresh-token";

		// useCookie = false 설정
		ReflectionTestUtils.setField(authService, "useCookie", false);

		// 토큰 검증 및 생성 설정
		when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
		when(jwtTokenProvider.getMemberId(refreshToken)).thenReturn(1L);
		when(oauthService.findUserById(1L)).thenReturn(testMember);
		when(jwtTokenProvider.createAccessToken(testMember)).thenReturn(newAccessToken);
		when(jwtTokenProvider.createRefreshToken(testMember)).thenReturn(newRefreshToken);

		// when
		LoginResponse response = authService.refreshToken(refreshToken, mockResponse);

		// then
		assertThat(response).isNotNull();
		assertThat(response.accessToken()).isEqualTo(newAccessToken);
		assertThat(response.userId()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("test@example.com");
		assertThat(response.nickname()).isEqualTo("testUser");
		assertThat(response.refreshToken()).isEqualTo(newRefreshToken); // 비쿠키 모드에서는 refreshToken이 응답에 포함됨

		// 기존 토큰 블랙리스트 추가 확인
		verify(jwtTokenProvider).blacklistRefreshToken(refreshToken);

		// 쿠키 추가되지 않음 확인
		verify(cookieUtil, never()).addCookie(any(), any());
	}

	@Test
	@DisplayName("빈 리프레시 토큰으로 토큰 재발급 시 예외가 발생한다")
	void refreshToken_emptyToken() {
		// given
		String refreshToken = "";

		// when & then
		assertThatThrownBy(() -> authService.refreshToken(refreshToken, mockResponse))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("리프레시 토큰이 없습니다");
	}

	@Test
	@DisplayName("유효하지 않은 리프레시 토큰으로 토큰 재발급 시 예외가 발생한다")
	void refreshToken_invalidToken() {
		// given
		String refreshToken = "invalid-refresh-token";

		// 토큰 검증 실패 설정
		when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> authService.refreshToken(refreshToken, mockResponse))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("유효하지 않은 리프레시 토큰입니다");
	}

	@Test
	@DisplayName("로그아웃 시 리프레시 토큰을 블랙리스트에 추가하고 쿠키를 삭제한다")
	void logout() {
		// given
		String refreshToken = "valid-refresh-token";

		// 쿠키 삭제 설정
		when(cookieUtil.deleteRefreshTokenCookie()).thenReturn(mockCookie);

		// when
		authService.logout(refreshToken, mockResponse);

		// then
		// 토큰 블랙리스트 추가 확인
		verify(jwtTokenProvider).blacklistRefreshToken(refreshToken);

		// 쿠키 삭제 확인
		verify(cookieUtil).addCookie(mockResponse, mockCookie);
	}

	@Test
	@DisplayName("빈 리프레시 토큰으로 로그아웃 시 블랙리스트에 추가하지 않고 쿠키만 삭제한다")
	void logout_emptyToken() {
		// given
		String refreshToken = "";

		// 쿠키 삭제 설정
		when(cookieUtil.deleteRefreshTokenCookie()).thenReturn(mockCookie);

		// when
		authService.logout(refreshToken, mockResponse);

		// then
		// 토큰 블랙리스트 추가하지 않음
		verify(jwtTokenProvider, never()).blacklistRefreshToken(anyString());

		// 쿠키 삭제 확인
		verify(cookieUtil).addCookie(mockResponse, mockCookie);
	}
}
