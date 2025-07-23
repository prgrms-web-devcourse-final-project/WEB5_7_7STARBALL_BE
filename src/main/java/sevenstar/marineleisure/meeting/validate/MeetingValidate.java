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

	// Rich Domain Model 리팩토링으로 불필요해진 메서드들
	// Meeting.isHost()로 대체됨
	/*
	@Transactional(readOnly = true)
	public void verifyIsHost(Long memberId, Long hostId){
		if(!Objects.equals(hostId, memberId)){
			throw new CustomException(MeetingError.MEETING_NOT_HOST);
		}
	}
	*/

	// Meeting.canJoin()로 대체됨
	/*
	@Transactional(readOnly = true)
	public void verifyRecruiting(Meeting meeting){
		if(meeting.getStatus() != MeetingStatus.RECRUITING){
			throw new CustomException(MeetingError.MEETING_NOT_RECRUITING);
		}
	}
	*/

	// Meeting.isFull()로 대체됨
	/*
	@Transactional(readOnly = true)
	public void verifyMeetingCount(int targetCount, Meeting meeting){
		if(targetCount >= meeting.getCapacity()){
			throw new CustomException(MeetingError.MEETING_ALREADY_FULL);
		}
	}
	*/

	// Meeting.removeParticipant()에서 처리됨
	/*
	@Transactional(readOnly = true)
	public void verifyNotHost(Long memberId, Meeting meeting){
		if(memberId.equals(meeting.getHostId())){
			throw new CustomException(MeetingError.MEETING_NOT_LEAVE_HOST);
		}
	}
	*/

	// Meeting.canLeave()로 대체됨
	/*
	@Transactional(readOnly = true)
	public void verifyLeave(Meeting meeting){
		if(meeting.getStatus() == MeetingStatus.COMPLETED || meeting.getStatus() == MeetingStatus.ONGOING){
			throw new CustomException(MeetingError.CANNOT_LEAVE_COMPLETED_MEETING);
		}
	}
	*/

}