package sevenstar.marineleisure.meeting.validate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.Repository.MeetingRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.error.MeetingError;

@Component
@RequiredArgsConstructor
@Transactional
public class MeetingValidate {

	private final MeetingRepository meetingRepository;


	public Meeting foundMeeting(Long meetingId){
		return meetingRepository.findById(meetingId)
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));

	}

	public void existMeeting(Long meetingId){
		if(!meetingRepository.existsById(meetingId)){
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}
	}


}