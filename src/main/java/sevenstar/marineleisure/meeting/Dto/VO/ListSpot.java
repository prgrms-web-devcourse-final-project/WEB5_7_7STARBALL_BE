package sevenstar.marineleisure.meeting.Dto.VO;

import lombok.Builder;

@Builder
public record ListSpot(
    long id,
    String name,
    String location
) {

}
