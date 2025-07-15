package sevenstar.marineleisure.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.global.jwt.JwtTokenProvider;
import sevenstar.marineleisure.member.dto.AuthCodeRequest;
import sevenstar.marineleisure.member.dto.LoginResponse;
import sevenstar.marineleisure.member.service.AuthService;
import sevenstar.marineleisure.member.service.OauthService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	// @MockBean → @MockitoBean
	@MockitoBean
	private AuthService authService;
	@MockitoBean
	private OauthService oauthService;
	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	private LoginResponse loginResponseCookie;
	private LoginResponse loginResponseNoCookie;

	@BeforeEach
	void setUp() {
		// 쿠키 모드용 응답 (refreshToken 없음)
		loginResponseCookie = LoginResponse.builder()
			.accessToken("test-access-token")
			.userId(1L)
			.email("test@example.com")
			.nickname("testUser")
			.build();

		// 비쿠키 모드용 응답 (refreshToken 포함)
		loginResponseNoCookie = LoginResponse.builder()
			.accessToken("test-access-token")
			.userId(1L)
			.email("test@example.com")
			.nickname("testUser")
			.refreshToken("test-refresh-token")
			.build();
	}

	@Test
	@DisplayName("카카오 로그인 URL을 요청할 수 있다")
	void getKakaoLoginUrl() throws Exception {
		Map<String, String> loginUrlInfo = new HashMap<>();
		loginUrlInfo.put("kakaoAuthUrl",
			"https://kauth.kakao.com/oauth/authorize?client_id=test-api-key"
				+ "&redirect_uri=http://localhost:8080/oauth/kakao/code"
				+ "&response_type=code&state=test-state");
		loginUrlInfo.put("state", "test-state");
		loginUrlInfo.put("encryptedState", "encrypted-test-state");
		loginUrlInfo.put("accessToken", "test-access-token");

		when(oauthService.getKakaoLoginUrl(isNull(), any())).thenReturn(loginUrlInfo);

		mockMvc.perform(get("/auth/kakao/url"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.kakaoAuthUrl").exists())
			.andExpect(jsonPath("$.body.state").value("test-state"))
			.andExpect(jsonPath("$.body.encryptedState").value("encrypted-test-state"))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"));
	}

	@Test
	@DisplayName("커스텀 리다이렉트 URI로 카카오 로그인 URL을 요청할 수 있다")
	void getKakaoLoginUrlWithCustomRedirectUri() throws Exception {
		String customRedirectUri = "http://custom-redirect.com/callback";
		Map<String, String> loginUrlInfo = new HashMap<>();
		loginUrlInfo.put("kakaoAuthUrl",
			"https://kauth.kakao.com/oauth/authorize?client_id=test-api-key"
				+ "&redirect_uri=" + customRedirectUri
				+ "&response_type=code&state=test-state");
		loginUrlInfo.put("state", "test-state");
		loginUrlInfo.put("encryptedState", "encrypted-test-state");
		loginUrlInfo.put("accessToken", "test-access-token");

		when(oauthService.getKakaoLoginUrl(any(), any())).thenReturn(loginUrlInfo);

		mockMvc.perform(get("/auth/kakao/url").param("redirectUri", customRedirectUri))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.kakaoAuthUrl").exists())
			.andExpect(jsonPath("$.body.state").value("test-state"))
			.andExpect(jsonPath("$.body.encryptedState").value("encrypted-test-state"))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"));
	}

	@Test
	@DisplayName("카카오 로그인을 처리할 수 있다 (쿠키 모드)")
	void kakaoLogin() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest("test-auth-code", "test-state", "encrypted-test-state", null,
			null);
		when(authService.processKakaoLogin(eq("test-auth-code"), eq("test-state"), eq("encrypted-test-state"), any(
			HttpServletResponse.class))).thenReturn(loginResponseCookie);

		mockMvc.perform(post("/auth/kakao/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"))
			.andExpect(jsonPath("$.body.userId").value(1))
			.andExpect(jsonPath("$.body.email").value("test@example.com"))
			.andExpect(jsonPath("$.body.nickname").value("testUser"))
			.andExpect(jsonPath("$.body.refreshToken").doesNotExist()); // 쿠키 모드에서는 refreshToken이 응답에 포함되지 않음
	}

	@Test
	@DisplayName("카카오 로그인을 처리할 수 있다 (비쿠키 모드)")
	void kakaoLogin_noCookie() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest("test-auth-code", "test-state", "encrypted-test-state", null,
			null);
		when(authService.processKakaoLogin(eq("test-auth-code"), eq("test-state"), eq("encrypted-test-state"), any(
			HttpServletResponse.class))).thenReturn(loginResponseNoCookie);

		mockMvc.perform(post("/auth/kakao/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"))
			.andExpect(jsonPath("$.body.userId").value(1))
			.andExpect(jsonPath("$.body.email").value("test@example.com"))
			.andExpect(jsonPath("$.body.nickname").value("testUser"))
			.andExpect(jsonPath("$.body.refreshToken").value("test-refresh-token")); // 비쿠키 모드에서는 refreshToken이 응답에 포함됨
	}

	@Test
	@DisplayName("카카오 로그인 처리 중 오류가 발생하면 에러 응답을 반환한다")
	void kakaoLogin_error() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest("invalid-code", "test-state", "encrypted-test-state", null, null);
		when(authService.processKakaoLogin(eq("invalid-code"), eq("test-state"), eq("encrypted-test-state"),
			any(HttpServletResponse.class)))
			.thenThrow(new RuntimeException("Failed to get access token from Kakao"));

		mockMvc.perform(post("/auth/kakao/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value(MemberErrorCode.KAKAO_LOGIN_ERROR.getCode()))
			.andExpect(jsonPath("$.message").value(MemberErrorCode.KAKAO_LOGIN_ERROR.getMessage()));
	}

	@Test
	@DisplayName("사용자가 카카오 로그인을 취소하면 취소 응답을 반환한다")
	void kakaoLogin_canceled() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest(null, "test-state", "encrypted-test-state", "access_denied",
			"User denied access");

		mockMvc.perform(post("/auth/kakao/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(1503))
			.andExpect(jsonPath("$.message").value("사용자가 카카오 로그인을 취소했습니다."));
	}

	@Test
	@DisplayName("카카오 로그인 중 다른 에러가 발생하면 에러 응답을 반환한다")
	void kakaoLogin_otherError() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest(null, "test-state", "encrypted-test-state", "server_error",
			"Internal server error");

		mockMvc.perform(post("/auth/kakao/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value(1500));
	}

	@Test
	@DisplayName("리프레시 토큰으로 새 토큰을 발급할 수 있다 (쿠키 모드)")
	void refreshToken() throws Exception {
		String refreshToken = "valid-refresh-token";
		when(authService.refreshToken(eq(refreshToken), any())).thenReturn(loginResponseCookie);

		mockMvc.perform(post("/auth/refresh")
				.cookie(new Cookie("refresh_token", refreshToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"))
			.andExpect(jsonPath("$.body.userId").value(1))
			.andExpect(jsonPath("$.body.email").value("test@example.com"))
			.andExpect(jsonPath("$.body.nickname").value("testUser"))
			.andExpect(jsonPath("$.body.refreshToken").doesNotExist()); // 쿠키 모드에서는 refreshToken이 응답에 포함되지 않음
	}

	@Test
	@DisplayName("리프레시 토큰으로 새 토큰을 발급할 수 있다 (비쿠키 모드)")
	void refreshToken_noCookie() throws Exception {
		String refreshToken = "valid-refresh-token";
		when(authService.refreshToken(eq(refreshToken), any())).thenReturn(loginResponseNoCookie);

		// 비쿠키 모드에서는 리프레시 토큰을 요청 본문에 포함
		mockMvc.perform(post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"refreshToken\":\"" + refreshToken + "\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"))
			.andExpect(jsonPath("$.body.userId").value(1))
			.andExpect(jsonPath("$.body.email").value("test@example.com"))
			.andExpect(jsonPath("$.body.nickname").value("testUser"))
			.andExpect(jsonPath("$.body.refreshToken").value("test-refresh-token")); // 비쿠키 모드에서는 refreshToken이 응답에 포함됨
	}

	// @Test
	// @DisplayName("리프레시 토큰이 없으면 400을 반환한다")
	// void refreshToken_noToken() throws Exception {
	// 	mockMvc.perform(post("/auth/refresh"))
	// 		.andExpect(status().isUnauthorized());   // 400만 검증
	// }

	@Test
	@DisplayName("유효하지 않은 리프레시 토큰으로 토큰 재발급 시 에러 응답을 반환한다 (쿠키 모드)")
	void refreshToken_invalidToken() throws Exception {
		String refreshToken = "invalid-refresh-token";
		when(authService.refreshToken(eq(refreshToken), any()))
			.thenThrow(new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

		mockMvc.perform(post("/auth/refresh")
				.cookie(new Cookie("refresh_token", refreshToken)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(1402))
			.andExpect(jsonPath("$.message").value("유효하지 않은 리프레시 토큰입니다."));
	}

	@Test
	@DisplayName("유효하지 않은 리프레시 토큰으로 토큰 재발급 시 에러 응답을 반환한다 (비쿠키 모드)")
	void refreshToken_invalidToken_noCookie() throws Exception {
		String refreshToken = "invalid-refresh-token";
		when(authService.refreshToken(eq(refreshToken), any()))
			.thenThrow(new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

		mockMvc.perform(post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"refreshToken\":\"" + refreshToken + "\"}"))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(1402))
			.andExpect(jsonPath("$.message").value("유효하지 않은 리프레시 토큰입니다."));
	}

	@Test
	@DisplayName("로그아웃을 처리할 수 있다")
	void logout() throws Exception {
		String refreshToken = "valid-refresh-token";

		mockMvc.perform(post("/auth/logout")
				.cookie(new Cookie("refresh_token", refreshToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200));
	}

	@Test
	@DisplayName("리프레시 토큰 없이도 로그아웃을 처리할 수 있다")
	void logout_noToken() throws Exception {
		mockMvc.perform(post("/auth/logout"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200));
	}
}
