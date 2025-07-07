package sevenstar.marineleisure.meeting.Dto.Response;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.MeetingRole;

@Builder
public record ParticipantResponse(
	long id,
	MeetingRole role,
	String nickName
) {
}
