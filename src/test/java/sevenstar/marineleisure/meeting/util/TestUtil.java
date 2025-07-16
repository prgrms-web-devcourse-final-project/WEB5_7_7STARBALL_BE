package sevenstar.marineleisure.meeting.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.repository.TagRepository;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;

public class TestUtil {

	public static List<Meeting> createMeetings(
		Member host, Member member, OutdoorSpot spot, MeetingRepository meetingRepository, TagRepository tagRepository, ParticipantRepository participantRepository) {
		List<Meeting> meetings = new ArrayList<>();
		ActivityCategory[] categories = ActivityCategory.values();

		for (MeetingStatus status : MeetingStatus.values()) {
			for (int i = 1; i <= 12; i++) {
				ActivityCategory category = categories[i % categories.length];
				Member currentHost = host; // 항상 host(testHostMember)를 호스트로 설정

				meetings.add(Meeting.builder()
					.hostId(currentHost.getId())
					.spotId(spot.getId())
					.title("모임" + i)
					.description("테스트 모임입니다.")
					.category(category)
					.status(status)
					.capacity(5 + i) // 다양한 인원
					.meetingTime(createMeetingTimeForStatus(status, i)) // 상태에 따른 시간 설정
					.build());
			}
		}

		List<Meeting> savedMeetings = meetingRepository.saveAll(meetings);

		// 각 미팅에 대해 Tag와 Participant 생성
		for (Meeting meeting : savedMeetings) {
			// Tag 생성
			Tag tag = Tag.builder()
				.meetingId(meeting.getId())
				.content(Arrays.asList("테스트", "모임"))
				.build();
			tagRepository.save(tag);

			// Participant 생성 (호스트는 항상 참여자)
			Participant hostParticipant = Participant.builder()
				.meetingId(meeting.getId())
				.userId(meeting.getHostId())
				.role(MeetingRole.HOST)
				.build();
			participantRepository.save(hostParticipant);

			// 다른 멤버를 게스트로 참여시킴
			Participant guestParticipant = Participant.builder()
				.meetingId(meeting.getId())
				.userId(member.getId())
				.role(MeetingRole.GUEST)
				.build();
			participantRepository.save(guestParticipant);
		}

		return savedMeetings;
	}

	private static LocalDateTime createMeetingTimeForStatus(MeetingStatus status, int offset) {
		switch (status) {
			case RECRUITING:
			case FULL:
				// 모집중, 인원마감 상태는 미래의 모임
				return LocalDateTime.now().plusDays(offset);
			case ONGOING:
				// 진행중 상태는 현재 또는 아주 최근의 모임
				return LocalDateTime.now().minusHours(offset);
			case COMPLETED:
				// 완료 상태는 과거의 모임
				return LocalDateTime.now().minusDays(offset);
			default:
				return LocalDateTime.now();
		}
	}

	public static void setupSecurityContext(Long userId, String email) {
		sevenstar.marineleisure.global.jwt.UserPrincipal userPrincipal =
			sevenstar.marineleisure.global.jwt.UserPrincipal.builder()
				.id(userId)
				.email(email)
				.authorities(java.util.Collections.emptyList())
				.build();

		org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
			new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
				userPrincipal, "password", userPrincipal.getAuthorities());

		org.springframework.security.core.context.SecurityContext context =
			org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		org.springframework.security.core.context.SecurityContextHolder.setContext(context);
	}

	public static void clearSecurityContext() {
		org.springframework.security.core.context.SecurityContextHolder.clearContext();
	}


}

