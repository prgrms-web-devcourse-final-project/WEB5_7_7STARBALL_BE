package sevenstar.marineleisure.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import sevenstar.marineleisure.member.dto.AuthCodeRequest;
import sevenstar.marineleisure.member.dto.LoginResponse;
import sevenstar.marineleisure.member.service.AuthService;
import sevenstar.marineleisure.member.service.OauthService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

	private LoginResponse loginResponse;

	@BeforeEach
	void setUp() {
		loginResponse = LoginResponse.builder()
			.accessToken("test-access-token")
			.userId(1L)
			.email("test@example.com")
			.nickname("testUser")
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

		when(oauthService.getKakaoLoginUrl(isNull())).thenReturn(loginUrlInfo);

		mockMvc.perform(get("/auth/kakao/url"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.kakaoAuthUrl").exists())
			.andExpect(jsonPath("$.body.state").value("test-state"));
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

		when(oauthService.getKakaoLoginUrl(customRedirectUri)).thenReturn(loginUrlInfo);

		mockMvc.perform(get("/auth/kakao/url").param("redirectUri", customRedirectUri))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.kakaoAuthUrl").exists())
			.andExpect(jsonPath("$.body.state").value("test-state"));
	}

	@Test
	@DisplayName("카카오 로그인을 처리할 수 있다")
	void kakaoLogin() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest("test-auth-code", "test-state");
		when(authService.processKakaoLogin(eq("test-auth-code"), any())).thenReturn(loginResponse);

		mockMvc.perform(post("/auth/kakao/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"))
			.andExpect(jsonPath("$.body.userId").value(1))
			.andExpect(jsonPath("$.body.email").value("test@example.com"))
			.andExpect(jsonPath("$.body.nickname").value("testUser"));
	}

	@Test
	@DisplayName("카카오 로그인 처리 중 오류가 발생하면 에러 응답을 반환한다")
	void kakaoLogin_error() throws Exception {
		AuthCodeRequest request = new AuthCodeRequest("invalid-code", "test-state");
		when(authService.processKakaoLogin(eq("invalid-code"), any()))
			.thenThrow(new RuntimeException("Failed to get access token from Kakao"));

		mockMvc.perform(post("/auth/kakao/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value(500))
			.andExpect(jsonPath("$.message")
				.value("카카오 로그인 처리 중 오류가 발생했습니다: Failed to get access token from Kakao"));
	}

	@Test
	@DisplayName("리프레시 토큰으로 새 토큰을 발급할 수 있다")
	void refreshToken() throws Exception {
		String refreshToken = "valid-refresh-token";
		when(authService.refreshToken(eq(refreshToken), any())).thenReturn(loginResponse);

		mockMvc.perform(post("/auth/refresh")
				.cookie(new Cookie("refresh_token", refreshToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"))
			.andExpect(jsonPath("$.body.userId").value(1))
			.andExpect(jsonPath("$.body.email").value("test@example.com"))
			.andExpect(jsonPath("$.body.nickname").value("testUser"));
	}

	@Test
	@DisplayName("리프레시 토큰이 없으면 400을 반환한다")
	void refreshToken_noToken() throws Exception {
		mockMvc.perform(post("/auth/refresh"))
			.andExpect(status().isBadRequest());   // 400만 검증
	}

	@Test
	@DisplayName("유효하지 않은 리프레시 토큰으로 토큰 재발급 시 에러 응답을 반환한다")
	void refreshToken_invalidToken() throws Exception {
		String refreshToken = "invalid-refresh-token";
		when(authService.refreshToken(eq(refreshToken), any()))
			.thenThrow(new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

		mockMvc.perform(post("/auth/refresh")
				.cookie(new Cookie("refresh_token", refreshToken)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.code").value(401))
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
