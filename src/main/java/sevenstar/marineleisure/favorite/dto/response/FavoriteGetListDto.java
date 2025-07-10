package sevenstar.marineleisure.favorite.dto.response;

import java.util.List;

import lombok.Builder;
import sevenstar.marineleisure.favorite.dto.vo.FavoriteItem;

/**
 *
 * @param favorites : 즐겨찾기장소 리스트
 * @param cursorId : 커서위치
 * @param size : 한번에 보여줄 아이템개수
 * @param hasNext : 다음 내용 존재여부
 */
@Builder
public record FavoriteGetListDto(List<FavoriteItem> favorites, Long cursorId, int size, boolean hasNext) {
}
