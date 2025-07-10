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
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final OutdoorSpotRepository spotRepository;

	@Override
	@Transactional
	public Long createFavorite(Long id) {

		return 0L;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FavoriteItemVO> searchFavorite(Long cursorId, int size) {
		//TODO : 현재 유저 받아오기

		Long currentMemberId = 1L;
		Pageable pageable = PageRequest.of(0, size + 1);
		List<FavoriteItemVO> result = favoriteRepository.findFavoritesByMemberIdAndCursorId(1L,
			cursorId, pageable);
		return result;
	}

	@Override
	@Transactional
	public void removeFavorite(Long id) {
		FavoriteSpot favorite = favoriteRepository.findById(id)
			.orElseThrow(() -> new CustomException(FavoriteErrorCode.FAVORITE_NOT_FOUND));
		//TODO : 현재 유저 받아오기
		Long currentMemberId = 1L;
		if (!favorite.getMemberId().equals(currentMemberId)) {
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
		// 즐겨찾기한 장소 유효성 검사
		FavoriteSpot favorite = favoriteRepository.findById(id)
			.orElseThrow(() -> new CustomException(FavoriteErrorCode.FAVORITE_NOT_FOUND));
		//유저 권한 검사
		Long currentMemberId = getCurrentUserId();
		if (!favorite.getMemberId().equals(currentMemberId)) {
			throw new CustomException(FavoriteErrorCode.FORBIDDEN_FAVORITE_ACCESS);
		}
		favorite.toggleNotification();
		return favorite;
	}
}
