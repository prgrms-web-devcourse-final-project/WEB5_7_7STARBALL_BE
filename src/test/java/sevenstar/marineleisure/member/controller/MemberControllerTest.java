package sevenstar.marineleisure.member.controller;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import sevenstar.marineleisure.global.enums.MemberStatus;
import sevenstar.marineleisure.global.util.CurrentUserUtil;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
import sevenstar.marineleisure.member.service.MemberService;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
	//@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 비활성화
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberService memberService;

	private MemberDetailResponse memberDetailResponse;

	@BeforeEach
	void setUp() {
		// 테스트용 응답 객체 생성
		memberDetailResponse = MemberDetailResponse.builder()
			.id(1L)
			.email("test@example.com")
			.nickname("testUser")
			.status(MemberStatus.ACTIVE)
			.latitude(BigDecimal.valueOf(37.5665))
			.longitude(BigDecimal.valueOf(126.9780))
			.build();
	}

	@Test
	@DisplayName("현재 로그인한 회원의 상세 정보를 조회할 수 있다")
	@WithMockUser
	void getCurrentMemberDetail() throws Exception {
		// given
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(1L);
			when(memberService.getCurrentMemberDetail(1L)).thenReturn(memberDetailResponse);

			// when & then
			mockMvc.perform(get("/members/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.body.id").value(1))
				.andExpect(jsonPath("$.body.email").value("test@example.com"))
				.andExpect(jsonPath("$.body.nickname").value("testUser"))
				.andExpect(jsonPath("$.body.status").value("ACTIVE"))
				.andExpect(jsonPath("$.body.latitude").value(37.5665))
				.andExpect(jsonPath("$.body.longitude").value(126.9780));
		}
	}

	@Test
	@DisplayName("인증되지 않은 사용자가 회원 정보 조회 시 401 이 발생한다")
	void getCurrentMemberDetail_notAuthenticated() throws Exception {
		// given
		mockMvc.perform(get("/members/me"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("존재하지 않는 회원 ID로 조회 시 예외가 발생한다")
	@WithMockUser
	void getCurrentMemberDetail_memberNotFound() {
		// given
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(999L);
			when(memberService.getCurrentMemberDetail(999L))
				.thenThrow(new NoSuchElementException("회원을 찾을 수 없습니다: 999"));

			// when & then
			ServletException ex = assertThrows(
				ServletException.class,
				() -> mockMvc.perform(get("/members/me"))
			);

			// 그리고 그 원인이 NoSuchElementException인지, 메시지는 맞는지 추가 검증
			Throwable cause = ex.getCause();
			assertThat(cause).isInstanceOf(NoSuchElementException.class);
			assertThat(cause.getMessage()).isEqualTo("회원을 찾을 수 없습니다: 999");
		}
	}

	@Test
	@DisplayName("현재 로그인한 회원을 소프트 삭제할 수 있다")
	@WithMockUser
	void deleteMember() throws Exception {
		// given
		Long currentUserId = 1L;
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentUserId);
			doNothing().when(memberService).deleteMember(currentUserId);

			// when & then
			mockMvc.perform(post("/members/delete"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.body").value("회원이 성공적으로 삭제되었습니다."));
		}
	}

	@Test
	@DisplayName("존재하지 않는 회원 ID로 삭제 시 예외가 발생한다")
	@WithMockUser
	void deleteMember_memberNotFound() {
		// given
		Long nonExistentMemberId = 999L;
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(nonExistentMemberId);
			Mockito.doThrow(new NoSuchElementException("회원을 찾을 수 없습니다: " + nonExistentMemberId))
				.when(memberService).deleteMember(nonExistentMemberId);

			// when & then
			ServletException ex = assertThrows(
				ServletException.class,
				() -> mockMvc.perform(post("/members/delete"))
			);

			// 그리고 그 원인이 NoSuchElementException인지, 메시지는 맞는지 추가 검증
			Throwable cause = ex.getCause();
			assertThat(cause).isInstanceOf(NoSuchElementException.class);
			assertThat(cause.getMessage()).isEqualTo("회원을 찾을 수 없습니다: 999");
		}
	}

	@Test
	@DisplayName("인증되지 않은 사용자가 회원 삭제 시 401 이 발생한다")
	void deleteMember_notAuthenticated() throws Exception {
		// given
		mockMvc.perform(post("/members/delete"))
			.andExpect(status().isUnauthorized());
	}
}
