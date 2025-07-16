package sevenstar.marineleisure.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import sevenstar.marineleisure.AbstractTest;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;
import sevenstar.marineleisure.member.service.OauthService;

// @SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationIntegrationTest extends AbstractTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private OauthService oauthService;

	private KakaoTokenResponse kakaoTokenResponse;
	private Map<String, Object> kakaoUserInfo;

	@BeforeEach
	void setUp() {
		// 카카오 토큰 응답 설정
		kakaoTokenResponse = KakaoTokenResponse.builder()
			.accessToken("kakao-access-token")
			.tokenType("bearer")
			.refreshToken("kakao-refresh-token")
			.expiresIn(3600L)
			.build();

		// 카카오 사용자 정보 설정
		kakaoUserInfo = new HashMap<>();
		kakaoUserInfo.put("id", 12345L);
		kakaoUserInfo.put("email", "test@example.com");
		kakaoUserInfo.put("nickname", "testUser");
	}

	// @Test
	// @DisplayName("전체 인증 흐름: 로그인 → 토큰 재발급 → 회원 정보 조회 → 로그아웃")
	// void fullAuthenticationFlow() throws Exception {
	//
	// 	when(oauthService.getKakaoLoginUrl(anyString())).thenReturn(stubUrl);
	// 	// 1. 카카오 로그인 URL 요청
	// 	MvcResult urlResult = mockMvc.perform(get("/auth/kakao/url"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.code").value(200))
	// 		.andExpect(jsonPath("$.body.kakaoAuthUrl").exists())
	// 		.andExpect(jsonPath("$.body.state").exists())
	// 		.andReturn();
	//
	// 	// 응답에서 state 추출
	// 	String responseJson = urlResult.getResponse().getContentAsString();
	// 	Map<String, Object> responseMap = objectMapper.readValue(responseJson, Map.class);
	// 	Map<String, String> body = (Map<String, String>)responseMap.get("body");
	// 	String state = body.get("state");
	//
	// 	// 2. 카카오 로그인 처리 모킹
	// 	when(oauthService.exchangeCodeForToken(anyString())).thenReturn(kakaoTokenResponse);
	// 	when(oauthService.processKakaoUser(anyString())).thenReturn(kakaoUserInfo);
	//
	// 	// 3. 카카오 로그인 요청
	// 	AuthCodeRequest authCodeRequest = new AuthCodeRequest("test-auth-code", state);
	// 	MvcResult loginResult = mockMvc.perform(post("/auth/kakao/code")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(authCodeRequest)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.code").value(200))
	// 		.andExpect(jsonPath("$.body.accessToken").exists())
	// 		.andExpect(jsonPath("$.body.userId").exists())
	// 		.andExpect(jsonPath("$.body.email").exists())
	// 		.andReturn();
	//
	// 	// 응답에서 액세스 토큰 추출
	// 	String loginResponseJson = loginResult.getResponse().getContentAsString();
	// 	Map<String, Object> loginResponseMap = objectMapper.readValue(loginResponseJson, Map.class);
	// 	Map<String, Object> loginBody = (Map<String, Object>)loginResponseMap.get("body");
	// 	String accessToken = (String)loginBody.get("accessToken");
	//
	// 	// 리프레시 토큰 쿠키 추출
	// 	Cookie refreshTokenCookie = loginResult.getResponse().getCookie("refresh_token");
	// 	assertThat(refreshTokenCookie).isNotNull();
	// 	String refreshToken = refreshTokenCookie.getValue();
	//
	// 	// 4. 액세스 토큰으로 회원 정보 조회
	// 	mockMvc.perform(get("/members/me")
	// 			.header("Authorization", "Bearer " + accessToken))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.code").value(200))
	// 		.andExpect(jsonPath("$.body.id").exists())
	// 		.andExpect(jsonPath("$.body.email").exists());
	//
	// 	// 5. 리프레시 토큰으로 토큰 재발급
	// 	MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
	// 			.cookie(refreshTokenCookie))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.code").value(200))
	// 		.andExpect(jsonPath("$.body.accessToken").exists())
	// 		.andReturn();
	//
	// 	// 새 리프레시 토큰 쿠키 확인
	// 	Cookie newRefreshTokenCookie = refreshResult.getResponse().getCookie("refresh_token");
	// 	assertThat(newRefreshTokenCookie).isNotNull();
	// 	assertThat(newRefreshTokenCookie.getValue()).isNotEqualTo(refreshToken);
	//
	// 	// 6. 로그아웃
	// 	mockMvc.perform(post("/auth/logout")
	// 			.cookie(newRefreshTokenCookie))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.code").value(200));
	//
	// 	// 로그아웃 후 쿠키 삭제 확인
	// 	Cookie logoutCookie = loginResult.getResponse().getCookie("refresh_token");
	// 	if (logoutCookie != null) {
	// 		assertThat(logoutCookie.getMaxAge()).isZero();
	// 	}
	// }

	@Test
	@DisplayName("인증 없이 보호된 리소스에 접근하면 400대 응답을 받는다")
	void accessProtectedResourceWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/members/me"))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("잘못된 액세스 토큰으로 보호된 리소스에 접근하면 401 Unauthorized 응답을 받는다")
	void accessProtectedResourceWithInvalidToken() throws Exception {
		mockMvc.perform(get("/members/me")
				.header("Authorization", "Bearer invalid-token"))
			.andExpect(status().isUnauthorized());
	}
}
