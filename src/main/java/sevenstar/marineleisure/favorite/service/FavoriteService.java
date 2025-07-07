package sevenstar.marineleisure.favorite.service;

import java.util.List;

import sevenstar.marineleisure.favorite.domain.FavoriteSpot;

public interface FavoriteService {

	/**
	 * [POST] /favorites
	 * @param id : 스팟 id
	 * @return 즐겨찾기 추가된 스팟 id
	 * 즐겨찾기 추가후 해당 스팟 id 반환
	 */
	public Long createFavorite(Long id);

	/**
	 * [GET] /favorites
	 * @param cursorId : 커서 위치
	 * @param size : 한번에 보여줄 아이템 크기
	 * @return 즐겨찾기 목록
	 */
	public List<FavoriteSpot> searchFavorite(Long cursorId, int size);

	/**
	 * [DELETE] /favorites/{id}
	 * @param id : 즐겨찾기 id
	 * 즐겨찾기 목록에서 삭제
	 */
	public void removeFavorite(Long id);

	/**
	 * [UPDATE] /favorites/{id}
	 * @param id : 즐겨찾기 id
	 * @return 해당 즐겨찾기 엔티티 반환
	 * 즐겨찾기의 알림설정 전환
	 */
	public FavoriteSpot updateNotification(Long id);
}
