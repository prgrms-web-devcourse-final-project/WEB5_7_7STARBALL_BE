package sevenstar.marineleisure.spot.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SpotReadRequest {
	@NotNull(message = "위도(latitude)는 필수입니다.")
	@DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
	@DecimalMax(value = "90.0", message = "위도는 90 이하이어야 합니다.")
	private Float latitude;

	@NotNull(message = "경도(longitude)는 필수입니다.")
	@DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
	@DecimalMax(value = "180.0", message = "경도는 180 이하이어야 합니다.")
	private Float longitude;

	private String category;

	public SpotReadRequest(Float latitude, Float longitude, String category) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.category = category;
	}
}
