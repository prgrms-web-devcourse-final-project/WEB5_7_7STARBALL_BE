package sevenstar.marineleisure.meeting.validate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.Repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.error.MeetingError;

@Component
@RequiredArgsConstructor
@Transactional
public class ParticipantValidate {
	private final ParticipantRepository participantRepository;

	public void existParticipant(Long memberId){
		if(!participantRepository.existsByUserId(memberId)){
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}
	}

}
