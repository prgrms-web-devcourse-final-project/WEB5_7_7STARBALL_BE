package sevenstar.marineleisure.spot.dto;

import sevenstar.marineleisure.global.enums.ActivityCategory;

public record EmailContent(
	Long spotId,
	String spotName,
	ActivityCategory category
) {
}
