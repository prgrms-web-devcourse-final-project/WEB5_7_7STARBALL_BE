package sevenstar.marineleisure.activity.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.activity.dto.reponse.ActivitySummaryResponse;
import sevenstar.marineleisure.activity.dto.request.ActivityIndexRequest;
import sevenstar.marineleisure.activity.service.ActivityService;
import sevenstar.marineleisure.activity.dto.reponse.ActivityDetailResponse;
import sevenstar.marineleisure.activity.dto.request.ActivityDetailRequest;
import sevenstar.marineleisure.global.enums.ActivityCategory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/index")
    public Map<String, ActivitySummaryResponse> getActivityIndex(@RequestBody ActivityIndexRequest activityIndexRequest) {
        return activityService.getActivitySummary(
            activityIndexRequest.latitude(),
            activityIndexRequest.longitude(),
            activityIndexRequest.global()
        );
    }

    @GetMapping("/{activity}/detail")
    public ActivityDetailResponse getActivityDetail(@PathVariable ActivityCategory activity, @RequestBody ActivityDetailRequest activityDetailRequest) {
        return activityService.getActivityDetail(activity, activityDetailRequest.latitude(), activityDetailRequest.longitude());
    }

    @GetMapping("/weather")
    public void getActivityWeather(@RequestBody ActivityDetailRequest activityDetailRequest) {
    }

}
