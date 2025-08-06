package sevenstar.marineleisure.meeting.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.dto.vo.DetailSpot;
import sevenstar.marineleisure.meeting.dto.vo.TagList;

/**
 *
 * @param id : meetingID 반환
 * @param title : meeting의 제목
 * @param category : FISHING , SURFING , DIVING , MUDFLAT
 * @param capacity : 총인원수
 * @param hostId : 모임장의 ID
 * @param hostNickName : 모임장의 닉네임
 * @param hostEmail : 모임장의 EMAIL
 * @param description : 모임의 설명
 * @param spot : 장소의 객체
 * @param meetingTime : 모임 예정시간
 * @param status : 상태 MeetingStatus.java 참고
 * @param createdAt : 만들어진 시간
 */
@Builder
public record MeetingDetailResponse(
	long id,
	String title,
	ActivityCategory category,
	long capacity,
	Integer currentParticipants,
	long hostId,
	String hostNickName,
	String hostEmail,
	String description,
	DetailSpot spot,
	LocalDateTime meetingTime,
	MeetingStatus status,
	LocalDateTime createdAt,
	TagList tag
) {

}
