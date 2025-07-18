package sevenstar.marineleisure.activity.controller;

import static sevenstar.marineleisure.global.exception.enums.ActivityErrorCode.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.activity.dto.reponse.ActivityDetailResponse;
import sevenstar.marineleisure.activity.dto.reponse.ActivitySummaryResponse;
import sevenstar.marineleisure.activity.dto.reponse.ActivityWeatherResponse;
import sevenstar.marineleisure.activity.dto.request.ActivityDetailRequest;
import sevenstar.marineleisure.activity.dto.request.ActivityIndexRequest;
import sevenstar.marineleisure.activity.dto.request.ActivityWeatherRequest;
import sevenstar.marineleisure.activity.service.ActivityService;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.enums.ActivityCategory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/index")
    public ResponseEntity<BaseResponse<Map<String, ActivitySummaryResponse>>> getActivityIndex(@RequestBody ActivityIndexRequest activityIndexRequest) {
        return BaseResponse.success(activityService.getActivitySummary(
            activityIndexRequest.latitude(),
            activityIndexRequest.longitude(),
            activityIndexRequest.global()
        ));
    }

    @GetMapping("/{activity}/detail")
    public ResponseEntity<BaseResponse<ActivityDetailResponse>> getActivityDetail(@PathVariable ActivityCategory activity, @RequestBody ActivityDetailRequest activityDetailRequest) {
        try {
            return BaseResponse.success(activityService.getActivityDetail(activity, activityDetailRequest.latitude(), activityDetailRequest.longitude()));
        } catch (RuntimeException e) {
            return BaseResponse.error(WEATHER_NOT_FOUND);
        }
    }

    @GetMapping("/weather")
    public ResponseEntity<BaseResponse<ActivityWeatherResponse>> getActivityWeather(@RequestBody ActivityWeatherRequest activityWeatherRequest) {
        try {
            return BaseResponse.success(activityService.getWeatherBySpot(activityWeatherRequest.latitude(), activityWeatherRequest.longitude()));
        }
        catch (Exception e) {
            return BaseResponse.error(INVALID_ACTIVITY);
        }
    }

}
