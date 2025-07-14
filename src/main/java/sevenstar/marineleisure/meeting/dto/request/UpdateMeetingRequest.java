package sevenstar.marineleisure.meeting.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.meeting.dto.vo.TagList;

@Builder
public record UpdateMeetingRequest(
	String title,
	ActivityCategory category,
	Integer capacity,
	LocalDateTime localDateTime,
	Long spotId,
	String description,
	TagList tag
)
	{
}
