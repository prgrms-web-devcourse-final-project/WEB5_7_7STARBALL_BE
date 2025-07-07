package sevenstar.marineleisure.meeting.Dto.VO;

import lombok.Builder;

@Builder
public record DetailSpot(
    long id,
    String title,
	String location,
    Double latitude,
    Double longitude
) {

}
