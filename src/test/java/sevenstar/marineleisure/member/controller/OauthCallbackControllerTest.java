// package sevenstar.marineleisure.member.controller;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
// import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
// import sevenstar.marineleisure.member.dto.AuthCodeRequest;
// import sevenstar.marineleisure.member.dto.LoginResponse;
// import sevenstar.marineleisure.member.service.AuthService;
//
// @WebMvcTest(
// 	controllers = OauthCallbackController.class,
// 	excludeAutoConfiguration = {
// 		HibernateJpaAutoConfiguration.class,
// 		JpaRepositoriesAutoConfiguration.class,
// 	}
// )
// @AutoConfigureMockMvc(addFilters = false)
// class OauthCallbackControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	// @MockBean 대신 @MockitoBean 사용
// 	@MockitoBean
// 	private AuthService authService;
//
// 	private LoginResponse loginResponse;
//
// 	@BeforeEach
// 	void setUp() {
// 		loginResponse = LoginResponse.builder()
// 			.accessToken("test-access-token")
// 			.userId(1L)
// 			.email("test@example.com")
// 			.nickname("testUser")
// 			.build();
// 	}
//
// 	/**
// 	 * 해당 부분은 get 메서드 존재하지 않아 주석 처리하였음
// 	 * @author gunwoong
// 	 * @throws Exception
// 	 */
// 	// @Test
// 	// @DisplayName("GET 요청으로 카카오 OAuth 콜백을 처리하고 index.html로 포워드한다")
// 	// void kakaoCallbackGet() throws Exception {
// 	// 	mockMvc.perform(post("/oauth/kakao/code")
// 	// 			.with(csrf())
// 	// 			.contentType(MediaType.APPLICATION_JSON)
// 	// 			.content(objectMapper.writeValueAsString(new AuthCodeRequest("test-auth-code", "test-state"))))
// 	// 		.andExpect(status().isOk())
// 	// 		.andExpect(forwardedUrl("/index.html"));
// 	// }
//
// 	@Test
// 	@DisplayName("POST 요청으로 카카오 OAuth 콜백을 처리하고 로그인 응답을 반환한다")
// 	void kakaoCallbackPost() throws Exception {
// 		AuthCodeRequest request = new AuthCodeRequest("test-auth-code", "test-state");
// 		when(authService.processKakaoLogin(eq("test-auth-code"), any(), any(), any()))
// 			.thenReturn(loginResponse);
//
// 		mockMvc.perform(post("/oauth/kakao/code")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value(200))
// 			.andExpect(jsonPath("$.message").value("Success"))
// 			.andExpect(jsonPath("$.body.accessToken").value("test-access-token"))
// 			.andExpect(jsonPath("$.body.userId").value(1))
// 			.andExpect(jsonPath("$.body.email").value("test@example.com"))
// 			.andExpect(jsonPath("$.body.nickname").value("testUser"));
// 	}
//
// 	@Test
// 	@DisplayName("POST 요청 처리 중 예외 발생 시 error payload 반환")
// 	void kakaoCallbackPost_error() throws Exception {
// 		AuthCodeRequest request = new AuthCodeRequest("invalid-code", "test-state");
// 		when(authService.processKakaoLogin(eq("invalid-code"), any(), any(), any()))
// 			.thenThrow(new RuntimeException("Failed to get access token from Kakao"));
//
// 		mockMvc.perform(post("/oauth/kakao/code")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isInternalServerError())
// 			.andExpect(jsonPath("$.code").value(MemberErrorCode.KAKAO_LOGIN_ERROR.getCode()))
// 			.andExpect(jsonPath("$.message").value(MemberErrorCode.KAKAO_LOGIN_ERROR.getMessage()));
// 	}
// }
