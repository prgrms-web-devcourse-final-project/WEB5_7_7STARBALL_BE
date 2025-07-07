package sevenstar.marineleisure.meeting.Dto.VO;

import lombok.Builder;

@Builder
public record ListSpot(
    Long id,
    String name,
    String location
) {

}
