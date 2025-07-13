package sevenstar.marineleisure.activity.dto.reponse;

public record ActivityWeatherResponse(
    String location,
    float windSpeed,
    float waveHeight,
    int waterTemp,
    int visibility
) {
}
