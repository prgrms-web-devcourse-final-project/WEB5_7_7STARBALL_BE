package sevenstar.marineleisure.meeting.global;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.repository.TagRepository;
import sevenstar.marineleisure.meeting.service.MeetingService;
import sevenstar.marineleisure.member.repository.MemberRepository;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@TestConfiguration
public class TestAppConfig {

	@Bean
	public MeetingService meetingService() {
		return Mockito.mock(MeetingService.class);
	}

	@Bean
	public MemberRepository memberRepository() {
		return Mockito.mock(MemberRepository.class);
	}

	@Bean
	public OutdoorSpotRepository outdoorSpotSpotRepository() {
		return Mockito.mock(OutdoorSpotRepository.class);
	}

	@Bean
	public TagRepository tagRepository() {
		return Mockito.mock(TagRepository.class);
	}

	@Bean
	public ParticipantRepository participantRepository() {
		return Mockito.mock(ParticipantRepository.class);
	}
}
