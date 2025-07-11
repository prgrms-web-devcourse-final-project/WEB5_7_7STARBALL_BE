package sevenstar.marineleisure.meeting.Dto.VO;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record DetailSpot(
    long id,
    String name,
	String location,
	BigDecimal latitude,
	BigDecimal longitude
) {

}
