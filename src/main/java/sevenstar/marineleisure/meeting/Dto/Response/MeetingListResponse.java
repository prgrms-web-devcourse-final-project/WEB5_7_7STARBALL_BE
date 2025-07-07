package sevenstar.marineleisure.meeting.Dto.Response;

import java.time.LocalDateTime;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.Dto.VO.ListSpot;
import sevenstar.marineleisure.meeting.Dto.VO.Tag;

@Builder
public record MeetingListResponse(
	long id,
	ActivityCategory category,
	Integer capacity,
	long currentParticipants,
	long hostId,
	String hostNickName,
	LocalDateTime meetingTime,
	MeetingStatus status,
	ListSpot spot,
	Tag tag
) {

}
