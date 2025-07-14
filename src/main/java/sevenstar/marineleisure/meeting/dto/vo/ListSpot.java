package sevenstar.marineleisure.meeting.dto.vo;

import lombok.Builder;

@Builder
public record ListSpot(
    long id,
    String name,
    String location
) {

}
