package sevenstar.marineleisure.spot.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import sevenstar.marineleisure.global.enums.ActivityCategory;

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

	@NotNull(message = "반경은 필수입니다.")
	@Positive(message = "반경은 양수여야 합니다.")
	@Max(value = 1000,message = "반경은 1000km 이하여야 합니다.")
	private Integer radius;

	private ActivityCategory category;

	public SpotReadRequest(Float latitude, Float longitude, Integer radius, ActivityCategory category) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.category = category;
	}
}
