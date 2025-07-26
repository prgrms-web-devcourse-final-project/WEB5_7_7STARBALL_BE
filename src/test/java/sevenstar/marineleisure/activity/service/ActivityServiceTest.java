package sevenstar.marineleisure.activity.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sevenstar.marineleisure.activity.dto.request.ActivityIndexRequest;

class ActivityServiceTest {
    ActivityService activityService;

    @BeforeEach
    void setUp() {

    }

    // getActivitySummary
    @Test
    @DisplayName("getActivitySummary - 성공")
    void getActivitySummary() {
        ActivityIndexRequest activityIndexRequest = new ActivityIndexRequest(1.123456, 1.123456, true);

        activityService.getActivitySummary(activityIndexRequest.latitude(), activityIndexRequest.longitude(), activityIndexRequest.global());

    }


    // 
    @Test
    @DisplayName("getActivityDetail")
    void getActivityDetail() {
        activityService.getActivityDetail();

    }

    @Test
    @DisplayName("getWeatherBySpot")
    void getWeatherBySpot() {
        activityService.getWeatherBySpot();

    }

}