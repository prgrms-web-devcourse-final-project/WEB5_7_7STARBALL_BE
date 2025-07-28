package sevenstar.marineleisure.spot.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.projection.BestSpotProjection;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BestSpot {
	private Long spotId;
	private String name;
	@Enumerated(EnumType.STRING)
	private TotalIndex totalIndex;
	private Integer monthView;
	private Integer weekView;

	public BestSpot(Long spotId, String name, TotalIndex totalIndex, Integer monthView, Integer weekView) {
		this.spotId = spotId;
		this.name = name;
		this.totalIndex = totalIndex;
		this.monthView = monthView;
		this.weekView = weekView;
	}

	public BestSpot(BestSpotProjection bestSpotProjection) {
		this.spotId = bestSpotProjection.getId();
		this.name = bestSpotProjection.getName();
		this.totalIndex = bestSpotProjection.getTotalIndex();
		this.monthView = bestSpotProjection.getMonthView();
		this.weekView = bestSpotProjection.getWeekView();
	}
}
