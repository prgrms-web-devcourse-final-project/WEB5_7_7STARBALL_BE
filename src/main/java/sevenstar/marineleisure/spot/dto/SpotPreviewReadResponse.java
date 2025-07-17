package sevenstar.marineleisure.spot.dto;

import sevenstar.marineleisure.spot.domain.BestSpot;

public record SpotPreviewReadResponse(BestSpot fishing, BestSpot mudflat, BestSpot surfing, BestSpot scuba) {
}
