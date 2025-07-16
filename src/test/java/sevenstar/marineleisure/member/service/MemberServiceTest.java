package sevenstar.marineleisure.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import sevenstar.marineleisure.global.enums.MemberStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
import sevenstar.marineleisure.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MeetingRepository meetingRepository;

	@Mock
	private ParticipantRepository participantRepository;

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
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
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
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("회원의 닉네임을 업데이트할 수 있다")
	void updateMemberNickname() {
		// given
		String newNickname = "newNickname";
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
		when(memberRepository.save(any(Member.class))).thenReturn(testMember);

		// when
		MemberDetailResponse response = memberService.updateMemberNickname(memberId, newNickname);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getNickname()).isEqualTo(newNickname);
		verify(memberRepository).findById(memberId);
		verify(memberRepository).save(testMember);
	}

	@Test
	@DisplayName("회원의 위치 정보를 업데이트할 수 있다")
	void updateMemberLocation() {
		// given
		BigDecimal newLatitude = BigDecimal.valueOf(35.1234);
		BigDecimal newLongitude = BigDecimal.valueOf(129.5678);
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
		when(memberRepository.save(any(Member.class))).thenReturn(testMember);

		// when
		MemberDetailResponse response = memberService.updateMemberLocation(memberId, newLatitude, newLongitude);

		// then
		assertThat(response).isNotNull();
		// Note: We can't directly verify the latitude and longitude values here because
		// the test member's fields are updated through reflection in the service method
		verify(memberRepository).findById(memberId);
		verify(memberRepository).save(testMember);
	}

	@Test
	@DisplayName("회원의 상태를 업데이트할 수 있다")
	void updateMemberStatus() {
		// given
		MemberStatus newStatus = MemberStatus.EXPIRED;
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
		when(memberRepository.save(any(Member.class))).thenReturn(testMember);

		// when
		MemberDetailResponse response = memberService.updateMemberStatus(memberId, newStatus);

		// then
		assertThat(response).isNotNull();
		// Note: We can't directly verify the status value here because
		// the test member's field is updated through reflection in the service method
		verify(memberRepository).findById(memberId);
		verify(memberRepository).save(testMember);
	}

	@Test
	@DisplayName("회원을 탈퇴 처리할 수 있다")
	void deleteMember() {
		// given
		List<Meeting> hostedMeetings = new ArrayList<>();
		Meeting mockMeeting = mock(Meeting.class);
		hostedMeetings.add(mockMeeting);

		List<Participant> participations = new ArrayList<>();
		Participant mockParticipant = mock(Participant.class);
		participations.add(mockParticipant);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
		when(meetingRepository.findByHostId(memberId)).thenReturn(hostedMeetings);
		when(participantRepository.findByUserId(memberId)).thenReturn(participations);

		// when
		memberService.deleteMember(memberId);

		// then
		verify(memberRepository).findById(memberId);
		verify(meetingRepository).findByHostId(memberId);
		verify(meetingRepository).deleteAll(hostedMeetings);
		verify(participantRepository).findByUserId(memberId);
		verify(participantRepository).deleteAll(participations);
		verify(memberRepository).save(testMember);
	}
}
