package sevenstar.marineleisure.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import sevenstar.marineleisure.global.enums.MemberStatus;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
import sevenstar.marineleisure.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberService memberService;

	private Member testMember;
	private Long memberId = 1L;

	@BeforeEach
	void setUp() {
		// 테스트용 Member 객체 생성
		testMember = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.latitude(BigDecimal.valueOf(37.5665))
			.longitude(BigDecimal.valueOf(126.9780))
			.build();

		// ID 설정 (리플렉션 사용)
		ReflectionTestUtils.setField(testMember, "id", memberId);
		ReflectionTestUtils.setField(testMember, "status", MemberStatus.ACTIVE);
	}

	@Test
	@DisplayName("회원 ID로 회원 상세 정보를 조회할 수 있다")
	void getMemberDetail() {
		// given
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

		// when
		MemberDetailResponse response = memberService.getMemberDetail(memberId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(memberId);
		assertThat(response.getEmail()).isEqualTo("test@example.com");
		assertThat(response.getNickname()).isEqualTo("testUser");
		assertThat(response.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(response.getLatitude()).isEqualTo(BigDecimal.valueOf(37.5665));
		assertThat(response.getLongitude()).isEqualTo(BigDecimal.valueOf(126.9780));
	}

	@Test
	@DisplayName("존재하지 않는 회원 ID로 조회 시 예외가 발생한다")
	void getMemberDetail_memberNotFound() {
		// given
		Long nonExistentMemberId = 999L;
		when(memberRepository.findById(nonExistentMemberId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.getMemberDetail(nonExistentMemberId))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessageContaining("회원을 찾을 수 없습니다");
	}

	@Test
	@DisplayName("현재 로그인한 회원의 상세 정보를 조회할 수 있다")
	void getCurrentMemberDetail() {
		// given
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

		// when
		MemberDetailResponse response = memberService.getCurrentMemberDetail(memberId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(memberId);
		assertThat(response.getEmail()).isEqualTo("test@example.com");
		assertThat(response.getNickname()).isEqualTo("testUser");
		assertThat(response.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(response.getLatitude()).isEqualTo(BigDecimal.valueOf(37.5665));
		assertThat(response.getLongitude()).isEqualTo(BigDecimal.valueOf(126.9780));
	}

	@Test
	@DisplayName("존재하지 않는 회원 ID로 현재 회원 조회 시 예외가 발생한다")
	void getCurrentMemberDetail_memberNotFound() {
		// given
		Long nonExistentMemberId = 999L;
		when(memberRepository.findById(nonExistentMemberId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.getCurrentMemberDetail(nonExistentMemberId))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessageContaining("회원을 찾을 수 없습니다");
	}
}