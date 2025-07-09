package sevenstar.marineleisure.spot.dto;

import java.math.BigDecimal;

public interface SpotDistanceProjection {
	Long getId();
	String getName();
	String getCategory();
	BigDecimal getLatitude();
	BigDecimal getLongitude();
	Double getDistance();
}
