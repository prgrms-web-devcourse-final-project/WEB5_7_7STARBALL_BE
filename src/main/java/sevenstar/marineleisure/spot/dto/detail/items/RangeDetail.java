package sevenstar.marineleisure.spot.dto.detail.items;

public record RangeDetail(
	float min,
	float max
) {
	public static RangeDetail of(float min, float max) {
		return new RangeDetail(min, max);
	}
}