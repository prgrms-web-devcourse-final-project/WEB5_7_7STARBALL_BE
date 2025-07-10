package sevenstar.marineleisure.activity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.activity.dto.request.ActivityIndexRequest;
import sevenstar.marineleisure.activity.service.ActivityService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/index")
    public void getActivityIndex(@RequestBody ActivityIndexRequest activityIndexRequest) {
        activityService.getActivitySummary(
            activityIndexRequest.latitude(),
            activityIndexRequest.longitude(),
            activityIndexRequest.global()
        );
    }

    @GetMapping("/{activieis}/detail")
    public void getActivityDetail(@PathVariable String activieis) {}

    @GetMapping("/weather")
    public void getActivityWeather() {}

}
