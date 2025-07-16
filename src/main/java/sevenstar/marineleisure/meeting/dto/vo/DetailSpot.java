package sevenstar.marineleisure.meeting.dto.vo;

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
