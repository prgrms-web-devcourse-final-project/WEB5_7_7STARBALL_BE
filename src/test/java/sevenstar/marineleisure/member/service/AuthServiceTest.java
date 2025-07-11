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
	}

	@Test
	@DisplayName("카카오 로그인을 처리하고 로그인 응답을 반환할 수 있다")
	void processKakaoLogin() {
		// given
		String code = "test-auth-code";
		String accessToken = "kakao-access-token";
		String jwtAccessToken = "jwt-access-token";
		String refreshToken = "jwt-refresh-token";

		// 카카오 토큰 응답 설정
		KakaoTokenResponse tokenResponse = KakaoTokenResponse.builder()
			.accessToken(accessToken)
			.tokenType("bearer")
			.refreshToken("kakao-refresh-token")
			.expiresIn(3600L)
			.build();

		// 쿠키 설정
		when(cookieUtil.createRefreshTokenCookie(refreshToken)).thenReturn(mockCookie);

		// 서비스 메서드 모킹
		when(oauthService.exchangeCodeForToken(code)).thenReturn(tokenResponse);
		when(oauthService.processKakaoUser(accessToken)).thenReturn(testMember);
		// findUserById는 이제 필요 없음 (processKakaoUser가 직접 Member를 반환)
		when(jwtTokenProvider.createAccessToken(testMember)).thenReturn(jwtAccessToken);
		when(jwtTokenProvider.createRefreshToken(testMember)).thenReturn(refreshToken);

		// when
		LoginResponse response = authService.processKakaoLogin(code, mockResponse);

		// then
		assertThat(response).isNotNull();
		assertThat(response.accessToken()).isEqualTo(jwtAccessToken);
		assertThat(response.userId()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("test@example.com");
		assertThat(response.nickname()).isEqualTo("testUser");

		// 쿠키 추가 확인
		verify(cookieUtil).addCookie(mockResponse, mockCookie);
	}

	@Test
	@DisplayName("카카오 액세스 토큰이 없으면 예외가 발생한다")
	void processKakaoLogin_noAccessToken() {
		// given
		String code = "test-auth-code";

		// 액세스 토큰이 없는 응답 설정
		KakaoTokenResponse tokenResponse = KakaoTokenResponse.builder()
			.accessToken(null)
			.tokenType("bearer")
			.refreshToken("kakao-refresh-token")
			.expiresIn(3600L)
			.build();

		when(oauthService.exchangeCodeForToken(code)).thenReturn(tokenResponse);

		// when & then
		assertThatThrownBy(() -> authService.processKakaoLogin(code, mockResponse))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Failed to get access token from Kakao");
	}

	@Test
	@DisplayName("리프레시 토큰으로 새 토큰을 발급할 수 있다")
	void refreshToken() {
		// given
		String refreshToken = "valid-refresh-token";
		String newAccessToken = "new-access-token";
		String newRefreshToken = "new-refresh-token";

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

		// 기존 토큰 블랙리스트 추가 확인
		verify(jwtTokenProvider).blacklistRefreshToken(refreshToken);

		// 새 쿠키 추가 확인
		verify(cookieUtil).addCookie(mockResponse, mockCookie);
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
