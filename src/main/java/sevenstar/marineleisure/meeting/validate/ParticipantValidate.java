package sevenstar.marineleisure.meeting.validate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.error.ParticipantError;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;

@Component
@RequiredArgsConstructor
public class ParticipantValidate {
	private final ParticipantRepository participantRepository;

	@Transactional(readOnly = true)
	public void existParticipant(Long memberId){
		if(!participantRepository.existsByUserId(memberId)){
			throw new CustomException(ParticipantError.PARTICIPANT_NOT_EXIST);
		}
	}

	@Transactional(readOnly = true)
	public Participant foundParticipantMeetingIdAndUserId(Long meetingId , Long memberId){
		return participantRepository.findByMeetingIdAndUserId(meetingId, memberId)
			.orElseThrow(() -> new CustomException(ParticipantError.PARTICIPANT_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public int getParticipantCount(Long meetingId){
		return participantRepository.countMeetingId(meetingId)
			.orElseThrow(() -> new CustomException(ParticipantError.PARTICIPANT_ERROR_COUNT));
	}

	@Transactional(readOnly = true)
	public void verifyNotAlreadyParticipant(Long meetingId, Long memberId){
		if(participantRepository.existsByMeetingIdAndUserId(meetingId, memberId)){
			throw new CustomException(ParticipantError.ALREADY_PARTICIPATING);
		}
	}

}
