package sevenstar.marineleisure.spot.dto;

public record SpotCreateRequest(
	Float latitude,
	Float longitude,
	String location
) {
}
