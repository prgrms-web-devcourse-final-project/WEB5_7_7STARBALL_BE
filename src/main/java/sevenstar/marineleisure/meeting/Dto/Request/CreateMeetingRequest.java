package sevenstar.marineleisure.meeting.Dto.Request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.ActivityCategory;


/**
 *
 * @param category : FISHING , SURFING  , DIVING  , MUDFLAT
 * @param capacity : 총 인원
 * @param title : Meeting 의 이름
 * @param meetingTime  : 모임 시간
 * @param spotId : 장소 Id
 * @param description : 모임 설명
 * @param tags : 모임 태그 -> 요청 받을떄는 tags로 받고 VO는 서비스 안에서 변환해야할 것 같습니다.
 */
@Builder
public record CreateMeetingRequest(
	ActivityCategory category,
	Integer capacity,
	String title,
	LocalDateTime meetingTime,
	Long spotId,
	String description,
	List<String> tags
) {
}
