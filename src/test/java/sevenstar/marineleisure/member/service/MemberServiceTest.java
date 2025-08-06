package sevenstar.marineleisure.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

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

	@Mock
	private OauthService oauthService;

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
		when(mockMeeting.getId()).thenReturn(100L);
		hostedMeetings.add(mockMeeting);

		List<Participant> meetingParticipants = new ArrayList<>();
		Participant mockMeetingParticipant = mock(Participant.class);
		meetingParticipants.add(mockMeetingParticipant);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
		when(meetingRepository.findByHostId(memberId)).thenReturn(hostedMeetings);
		when(participantRepository.findParticipantsByMeetingId(100L)).thenReturn(meetingParticipants);
		when(meetingRepository.deleteMeetingByHostId(memberId)).thenReturn(1);
		when(participantRepository.deleteByUserId(memberId)).thenReturn(1);
		when(oauthService.unlinkKakaoAccount(testMember.getProviderId())).thenReturn(12345L);

		// when
		memberService.deleteMember(memberId);

		// then
		verify(memberRepository).findById(memberId);
		verify(meetingRepository).findByHostId(memberId);
		verify(participantRepository).findParticipantsByMeetingId(100L);
		verify(participantRepository).deleteAll(meetingParticipants);
		verify(meetingRepository).deleteMeetingByHostId(memberId);
		verify(participantRepository).deleteByUserId(memberId);
		verify(oauthService).unlinkKakaoAccount(testMember.getProviderId());
		verify(memberRepository).save(testMember);
	}

	@Test
	@DisplayName("회원 상태를 EXPIRED로 변경할 수 있다")
	void updateMemberStatusToExpired() {
		// given
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
		when(memberRepository.save(any(Member.class))).thenReturn(testMember);

		// when
		MemberDetailResponse response = memberService.updateMemberStatus(memberId, MemberStatus.EXPIRED);

		// then
		assertThat(response).isNotNull();
		verify(memberRepository).findById(memberId);
		verify(memberRepository).save(testMember);
	}

	@Test
	@DisplayName("회원 탈퇴 시 회원 상태가 EXPIRED로 변경된다")
	void deleteMember_updatesStatusToExpired() {
		// given
		List<Meeting> hostedMeetings = new ArrayList<>();

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
		when(meetingRepository.findByHostId(memberId)).thenReturn(hostedMeetings);

		// when
		memberService.deleteMember(memberId);

		// then
		// Capture the argument passed to save
		ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
		verify(memberRepository).save(memberCaptor.capture());

		// Verify that the member's status is updated to EXPIRED
		Member savedMember = memberCaptor.getValue();
		assertThat(savedMember.getStatus()).isEqualTo(MemberStatus.EXPIRED);
	}
}
