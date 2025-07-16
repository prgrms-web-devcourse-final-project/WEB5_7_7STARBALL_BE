package sevenstar.marineleisure.meeting.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.dto.vo.DetailSpot;
import sevenstar.marineleisure.meeting.dto.vo.TagList;

/**
 *
 * @param id : meeting id
 * @param title : meeting
 * @param category : FISHING, SURFING , DIVING , MUDFLAT
 * @param capacity : 총인원수
 * @param hostId : 모임하는 사람의 ID
 * @param hostNickName : 모임하는 사람의 NickName
 * @param hostEmail : 모임하는 사람의 EMAIL
 * @param description : 모임의 설명
 * @param spot : SPOT객체를 줍니다. (값객체로 Response할 예정)
 * @param meetingTime : 모임 시간 설정
 * @param status : 모임의 상태 : RECRUITING , ONGOING , FULL , COMPLETED
 * @param participants :  참여한 인원의 수 ( 값객체로 변환 )
 * @param createdAt : 생성시간
 */
@Builder
public record MeetingDetailAndMemberResponse(
	long id,
	String title,
	ActivityCategory category,
	long capacity,
	long hostId,
	String hostNickName,
	String hostEmail,
	String description,
	DetailSpot spot,
	LocalDateTime meetingTime,
	MeetingStatus status,
	List<ParticipantResponse> participants,
	TagList tagList,
	LocalDateTime createdAt
) {
}
