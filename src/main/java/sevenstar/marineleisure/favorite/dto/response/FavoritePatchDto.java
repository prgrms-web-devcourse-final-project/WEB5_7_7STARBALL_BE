package sevenstar.marineleisure.favorite.dto.response;

import lombok.Builder;

/**
 *
 * @param favoriteId : 즐겨찾기 id
 * @param notification : 현재 알림 상황
 */
@Builder
public record FavoritePatchDto(Long favoriteId, boolean notification) {
}
