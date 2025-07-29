package sevenstar.marineleisure.favorite.dto.vo;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.ActivityCategory;

/**
 *
 * @param id : 즐겨찾기 id
 * @param name : 장소이름
 * @param category : 장소의 활동 목적 구분
 * @param location : 위치
 * @param notification : 알림 여부
 */
@Builder
public record FavoriteItemVO(Long spotId, Long id, String name, ActivityCategory category, String location,
							 boolean notification) {
}
