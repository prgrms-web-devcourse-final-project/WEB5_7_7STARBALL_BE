package sevenstar.marineleisure.activity.dto.reponse;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.TotalIndex;

public class ActivityDetailResponse {
    String category;
    String location;
    // int currentIndex;
    // String currentLevel;
    List<ForecastResponse> forecast;
    // FactorsResponse factors;
    // RecommendationsResponse recommendations;

    @Builder
    public ActivityDetailResponse(String category, String location, List<?> listActivity) {
        this.category = category;
        this.location = location;
        // this.forecast = listActivity.stream().map(activity -> new ForecastResponse(
        //     activity.getCreatedAt(),
        //     activity.getTotalIndex();
        // ));
    }

}

class ForecastResponse {
    LocalDate date;
    TotalIndex totalIndex;
    String level;
}

class FactorsResponse {
    String waterTemp;
    String tide;
    String waveHeight;
    String windSpeed;
}

class RecommendationsResponse {
    int pointId;
    String name;
    float distance;
    int currentIndex;
    String reason;
}