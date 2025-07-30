package sevenstar.marineleisure.meeting.dto.response;

import lombok.Builder;

@Builder
public record GoingMeetingResponse(
    Long meetingId
) {
}