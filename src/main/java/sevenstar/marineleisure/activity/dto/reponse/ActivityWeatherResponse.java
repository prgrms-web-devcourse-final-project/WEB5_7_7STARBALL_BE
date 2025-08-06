package sevenstar.marineleisure.activity.dto.reponse;

public record ActivityWeatherResponse(
    String location,
    String windSpeed,
    String waveHeight,
    String waterTemp,
	Long spotId
) {
}
