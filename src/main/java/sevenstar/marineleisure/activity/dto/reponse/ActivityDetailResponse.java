package sevenstar.marineleisure.activity.dto.reponse;

import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.ActivityDetail;

public record ActivityDetailResponse(
    String category,
    String location,
    ActivityDetail activityDetail
) {
}