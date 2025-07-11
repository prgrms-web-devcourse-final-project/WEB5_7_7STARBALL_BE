package sevenstar.marineleisure.favorite.service;

import static sevenstar.marineleisure.global.util.CurrentUserUtil.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.dto.vo.FavoriteItemVO;
import sevenstar.marineleisure.favorite.repository.FavoriteRepository;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.FavoriteErrorCode;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final OutdoorSpotRepository spotRepository;

	/**
	 * id로 즐겨찾기 추출 및 유효성 검사
	 * @param id : 즐겨찾기 id
	 * @return 즐겨찾기 객체
	 */
	public FavoriteSpot searchFavoriteById(Long id) {
		return favoriteRepository.findById(id)
			.orElseThrow(() -> new CustomException(FavoriteErrorCode.FAVORITE_NOT_FOUND));
	}

	/**
	 * 스팟id로 즐겨찾기 추가입니다.
	 * @param id : 스팟 id
	 * @return 즐겨찾기한 스팟 id
	 */
	@Override
	@Transactional
	public Long createFavorite(Long id) {
		Long currentMemberId = getCurrentUserId();
		// 우선 즐겨찾기를 못찾았다고 넣었지만, 나중에 Spot에러코드 추가되면 그걸로 교체 예정입니다.
		OutdoorSpot outdoorSpot = spotRepository.findById(id)
			.orElseThrow(() -> new CustomException(FavoriteErrorCode.FAVORITE_NOT_FOUND));

		FavoriteSpot createdFavoriteSpot = FavoriteSpot.builder()
			.memberId(currentMemberId)
			.spotId(outdoorSpot.getId())
			.build();

		favoriteRepository.save(createdFavoriteSpot);
		return id;
	}

	/**
	 * 즐겨찾기 목록을 반환합니다.
	 * @param cursorId : 커서 위치
	 * @param size : 한번에 보여줄 아이템 크기
	 * @return : 사용자에게 보여줄 즐겨찾기 내용객체 리스트
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FavoriteItemVO> searchFavorite(Long cursorId, int size) {
		Long currentMemberId = getCurrentUserId();

		Pageable pageable = PageRequest.of(0, size + 1);
		List<FavoriteItemVO> result = favoriteRepository.findFavoritesByMemberIdAndCursorId(currentMemberId,
			cursorId, pageable);

		return result;
	}

	/**
	 * 즐겨찾기 목록에서 삭제
	 * @param id : 즐겨찾기 id
	 */
	@Override
	@Transactional
	public void removeFavorite(Long id) {
		FavoriteSpot favoriteSpot = searchFavoriteById(id);

		//유저 권한 검사
		Long currentMemberId = getCurrentUserId();
		if (!favoriteSpot.getMemberId().equals(currentMemberId)) {
			throw new CustomException(FavoriteErrorCode.FORBIDDEN_FAVORITE_ACCESS);
		}

		favoriteRepository.deleteFavoriteSpotById(id);
	}

	/**
	 * 즐겨찾기 업데이트
	 * @param id : 즐겨찾기 id
	 * @return 업데이트한 즐겨찾기 객체
	 */
	@Override
	@Transactional
	public FavoriteSpot updateNotification(Long id) {
		FavoriteSpot favoriteSpot = searchFavoriteById(id);

		//유저 권한 검사
		Long currentMemberId = getCurrentUserId();
		if (!favoriteSpot.getMemberId().equals(currentMemberId)) {
			throw new CustomException(FavoriteErrorCode.FORBIDDEN_FAVORITE_ACCESS);
		}

		favoriteSpot.toggleNotification();
		return favoriteSpot;
	}
}
