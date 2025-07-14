package sevenstar.marineleisure.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import sevenstar.marineleisure.global.enums.MemberStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.global.util.CurrentUserUtil;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
import sevenstar.marineleisure.member.dto.MemberLocationUpdateRequest;
import sevenstar.marineleisure.member.dto.MemberNicknameUpdateRequest;
import sevenstar.marineleisure.member.dto.MemberStatusUpdateRequest;
import sevenstar.marineleisure.member.service.MemberService;

@WebMvcTest(MemberController.class)
	//@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 비활성화
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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
	void getCurrentMemberDetail_memberNotFound() throws Exception {
		// given
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			Long currentUserId = 999L;
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentUserId);
			when(memberService.getCurrentMemberDetail(currentUserId))
				.thenThrow(new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

			// when & then
			// ServletException ex = assertThrows(
			// 	ServletException.class,
			// 	() -> mockMvc.perform(get("/members/me"))
			// );
			//
			// // 그리고 그 원인이 NoSuchElementException인지, 메시지는 맞는지 추가 검증
			// Throwable cause = ex.getCause();
			// assertThat(cause).isInstanceOf(NoSuchElementException.class);
			// assertThat(cause.getMessage()).isEqualTo("회원을 찾을 수 없습니다: 999");
			mockMvc.perform(get("/members/me"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()));
		}
	}

	@Test
	@DisplayName("회원 닉네임을 업데이트할 수 있다")
	@WithMockUser
	void updateMemberNickname() throws Exception {
		// given
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			Long currentUserId = 1L;
			String newNickname = "newNickname";
			MemberNicknameUpdateRequest request = new MemberNicknameUpdateRequest(newNickname);

			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentUserId);

			// 업데이트된 회원 정보 설정
			MemberDetailResponse updatedMember = MemberDetailResponse.builder()
				.id(currentUserId)
				.email("test@example.com")
				.nickname(newNickname) // 새 닉네임으로 업데이트
				.status(MemberStatus.ACTIVE)
				.latitude(BigDecimal.valueOf(37.5665))
				.longitude(BigDecimal.valueOf(126.9780))
				.build();

			when(memberService.updateMemberNickname(eq(currentUserId), eq(newNickname)))
				.thenReturn(updatedMember);

			// when & then
			mockMvc.perform(put("/members/me")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.body.id").value(currentUserId))
				.andExpect(jsonPath("$.body.nickname").value(newNickname));
		}
	}

	@Test
	@DisplayName("회원 위치 정보를 업데이트할 수 있다")
	@WithMockUser
	void updateMemberLocation() throws Exception {
		// given
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			Long currentUserId = 1L;
			BigDecimal newLatitude = BigDecimal.valueOf(35.1234);
			BigDecimal newLongitude = BigDecimal.valueOf(129.5678);
			MemberLocationUpdateRequest request = new MemberLocationUpdateRequest(newLatitude, newLongitude);

			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentUserId);

			// 업데이트된 회원 정보 설정
			MemberDetailResponse updatedMember = MemberDetailResponse.builder()
				.id(currentUserId)
				.email("test@example.com")
				.nickname("testUser")
				.status(MemberStatus.ACTIVE)
				.latitude(newLatitude) // 새 위도로 업데이트
				.longitude(newLongitude) // 새 경도로 업데이트
				.build();

			when(memberService.updateMemberLocation(eq(currentUserId), eq(newLatitude), eq(newLongitude)))
				.thenReturn(updatedMember);

			// when & then
			mockMvc.perform(put("/members/me/location")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.body.id").value(currentUserId))
				.andExpect(jsonPath("$.body.latitude").value(35.1234))
				.andExpect(jsonPath("$.body.longitude").value(129.5678));
		}
	}

	@Test
	@DisplayName("회원 상태를 업데이트할 수 있다")
	@WithMockUser
	void updateMemberStatus() throws Exception {
		// given
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			Long currentUserId = 1L;
			MemberStatus newStatus = MemberStatus.EXPIRED;
			MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(newStatus);

			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentUserId);

			// 업데이트된 회원 정보 설정
			MemberDetailResponse updatedMember = MemberDetailResponse.builder()
				.id(currentUserId)
				.email("test@example.com")
				.nickname("testUser")
				.status(newStatus) // 새 상태로 업데이트
				.latitude(BigDecimal.valueOf(37.5665))
				.longitude(BigDecimal.valueOf(126.9780))
				.build();

			when(memberService.updateMemberStatus(eq(currentUserId), eq(newStatus)))
				.thenReturn(updatedMember);

			// when & then
			mockMvc.perform(patch("/members/me/status")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.body.id").value(currentUserId))
				.andExpect(jsonPath("$.body.status").value("EXPIRED"));
		}
	}

	@Test
	@DisplayName("회원을 소프트 삭제할 수 있다")
	@WithMockUser
	void deleteMember() throws Exception {
		// given
		try (MockedStatic<CurrentUserUtil> mockedStatic = Mockito.mockStatic(CurrentUserUtil.class)) {
			Long currentUserId = 1L;
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentUserId);

			// 서비스 메서드 모킹 (void 메서드)
			doNothing().when(memberService).deleteMember(currentUserId);

			// when & then
			mockMvc.perform(post("/members/delete"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.body").value("회원이 성공적으로 삭제되었습니다."));
		}
	}
}
