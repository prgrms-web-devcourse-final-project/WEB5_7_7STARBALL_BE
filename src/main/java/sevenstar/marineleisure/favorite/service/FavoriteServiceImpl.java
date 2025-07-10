package sevenstar.marineleisure.favorite.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.repository.FavoriteRepository;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.FavoriteErrorCode;
import sevenstar.marineleisure.member.domain.Member;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
	private final FavoriteRepository favoriteRepository;

	@Override
	public Long createFavorite(Member member, Long id) {
		return 0L;
	}

	@Override
	public List<FavoriteSpot> searchFavorite(Member member, Long cursorId, int size) {
		return List.of();
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

	@Override
	@Transactional
	public FavoriteSpot updateNotification(Long id) {
		return null;
	}
}
