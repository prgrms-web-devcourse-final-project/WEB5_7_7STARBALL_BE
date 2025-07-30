package sevenstar.marineleisure.meeting.validate;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.error.MeetingError;

@Component
@RequiredArgsConstructor
public class MeetingValidate {

	private final MeetingRepository meetingRepository;

	@Transactional(readOnly = true)
	public Meeting foundMeeting(Long meetingId){
		return meetingRepository.findById(meetingId)
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));

	}

	@Transactional(readOnly = true)
	public void validateHost(Meeting targetMeeting , Long memberId){
		if(!(targetMeeting.getHostId()).equals(memberId)){
			throw new CustomException(MeetingError.MEETING_NOT_HOST);
		}
	}

	@Transactional(readOnly = true)
	public void validateStatus(Meeting targetMeeting){
		if(targetMeeting.getStatus()==MeetingStatus.COMPLETED || targetMeeting.getStatus() == MeetingStatus.ONGOING){
			throw new CustomException(MeetingError.CANNOT_CHANGE_GOING_STATUS);
		}
	}

}