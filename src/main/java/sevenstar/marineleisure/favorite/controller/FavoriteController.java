package sevenstar.marineleisure.favorite.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.dto.response.FavoriteGetListDto;
import sevenstar.marineleisure.favorite.dto.response.FavoritePatchDto;
import sevenstar.marineleisure.favorite.dto.vo.FavoriteItemVO;
import sevenstar.marineleisure.favorite.mapper.FavoriteMapper;
import sevenstar.marineleisure.favorite.service.FavoriteServiceImpl;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.FavoriteErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite")
public class FavoriteController {

	private final FavoriteServiceImpl service;
	private final FavoriteMapper mapper;

	/**
	 * 스팟id로 로그인 유저의 즐겨찾기 목록에 추가
	 * @param id : 즐겨찾기에 추가할 spotId
	 * @return 즐겨찾기 추가된 스팟 id
	 */
	@PostMapping("/{id}")
	public ResponseEntity<BaseResponse<Long>> addFavorite(@PathVariable Long id) {
		service.createFavorite(id);
		return BaseResponse.success(id);
	}

	/**
	 * 현재 로그인 유저의 즐겨찾기 목록 반환
	 * @return 즐겨찾기 목록
	 */
	@GetMapping
	public ResponseEntity<BaseResponse<FavoriteGetListDto>> searchFavorites(
		@RequestParam(defaultValue = "0") Long cursorId,
		@RequestParam(defaultValue = "20") @Min(1) @Max(10) int size) {
		List<FavoriteItemVO> result = service.searchFavorite(cursorId, size);

		boolean hasNext = result.size() > size;
		List<FavoriteItemVO> items = hasNext ? result.subList(0, size) : result;
		
		return BaseResponse.success(new FavoriteGetListDto(items, cursorId, size, hasNext));
	}

	/**
	 * 즐겨찾기 id로 삭제
	 * @param id : 즐겨찾기 id
	 * @return body가 없음
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseResponse<Void>> removeFavorites(@PathVariable Long id) {
		// 즐겨찾기 id 형식 검사
		if (id == null || id <= 0) {
			throw new CustomException(FavoriteErrorCode.INVALID_FAVORITE_PARAMETER);
		}
		service.removeFavorite(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 즐겨찾기 id로 해당 스팟에 대한 알림 기능 활성화 비활성화
	 * @param id : 즐겨찾기 id
	 * @return 즐겨찾기 id,현재 알림 상태
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<BaseResponse<FavoritePatchDto>> updateFavorites(@PathVariable Long id) {
		// 즐겨찾기 id 형식 검사
		if (id == null || id <= 0) {
			throw new CustomException(FavoriteErrorCode.INVALID_FAVORITE_PARAMETER);
		}
		FavoriteSpot updatedSpot = service.updateNotification(id);
		return BaseResponse.success(mapper.toPatchDto(updatedSpot));
	}

}
