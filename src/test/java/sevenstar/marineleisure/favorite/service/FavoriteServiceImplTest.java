package sevenstar.marineleisure.favorite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.dto.vo.FavoriteItemVO;
import sevenstar.marineleisure.favorite.repository.FavoriteRepository;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.FavoriteErrorCode;
import sevenstar.marineleisure.global.util.CurrentUserUtil;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

	@Mock
	private FavoriteRepository favoriteRepository;

	@Mock
	private OutdoorSpotRepository outdoorSpotRepository;

	@InjectMocks
	private FavoriteServiceImpl service;

	private Long currentMemberId;
	private Long favorite1Id;
	private Long favorite2Id;
	private Long spot1Id;

	@BeforeEach
	void setUp() {
		currentMemberId = 1L;
		favorite1Id = 1L;
		favorite2Id = 2L;
		spot1Id = 1L;
	}

	@Test
	@DisplayName("즐겨찾기 유효성 검사 - 성공")
	void availbleTrue() {
		//given
		FavoriteSpot fav1 = mock(FavoriteSpot.class);
		fav1 = mock(FavoriteSpot.class);
		when(fav1.getSpotId()).thenReturn(favorite1Id);
		when(fav1.getMemberId()).thenReturn(currentMemberId);
		given(favoriteRepository.findById(favorite1Id)).willReturn(Optional.of(fav1));

		//when
		FavoriteSpot result = service.searchFavoriteById(favorite1Id);

		//then
		assertNotNull(result);
		assertEquals(fav1.getMemberId(), result.getMemberId());
		assertEquals(fav1.getSpotId(), result.getSpotId());
	}

	@Test
	@DisplayName("즐겨찾기 유효성 검사 - 실패")
	void unavailableFalse() {
		//given
		given(favoriteRepository.findById(favorite1Id)).willReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> service.searchFavoriteById(favorite1Id));
		assertEquals(FavoriteErrorCode.FAVORITE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("즐겨찾기 생성 성공")
	void createFavorite_Sucess() {
		//given
		OutdoorSpot spot1 = mock(OutdoorSpot.class);
		FavoriteSpot fav1 = mock(FavoriteSpot.class);

		spot1 = mock(OutdoorSpot.class);
		fav1 = mock(FavoriteSpot.class);

		try (MockedStatic<CurrentUserUtil> mockedStatic = mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentMemberId);

			given(outdoorSpotRepository.findById(spot1Id))
				.willReturn(Optional.of(spot1));
			given(favoriteRepository.save(any(FavoriteSpot.class)))
				.willReturn(fav1);

			//when
			Long result = service.createFavorite(spot1Id);

			//then
			assertEquals(spot1Id, result);
			verify(favoriteRepository).save(any(FavoriteSpot.class));
		}
	}

	// @Test
	// @DisplayName("즐겨찾기 생성 실패 - 존재하지 않는 스팟")
	// void createFavorite_SpotNotFound() {
	// 	// given
	// 	try (MockedStatic<CurrentUserUtil> mockedStatic = mockStatic(CurrentUserUtil.class)) {
	// 		mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentMemberId);
	//
	// 		given(outdoorSpotRepository.findById(spot1Id))
	// 			.willReturn(Optional.empty());
	//
	// 		// when & then
	// 		CustomException exception = assertThrows(CustomException.class,
	// 			() -> service.createFavorite(spot1Id));
	// 		assertEquals(FavoriteErrorCode.FAVORITE_NOT_FOUND, exception.getErrorCode());
	// 	}
	// }

	@Test
	@DisplayName("즐겨찾기 목록 조회 성공")
	void searchFavorite_Sucess() {
		//given
		Long cursorId = 0L;
		int size = 1;
		try (MockedStatic<CurrentUserUtil> mockedStatic = mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentMemberId);

			List<FavoriteItemVO> mockResult = List.of(
				FavoriteItemVO.builder().build(),
				FavoriteItemVO.builder().build()
			);

			Pageable pageable = PageRequest.of(0, size + 1);
			given(favoriteRepository.findFavoritesByMemberIdAndCursorId(currentMemberId, cursorId, pageable))
				.willReturn(mockResult);

			//when
			List<FavoriteItemVO> result = service.searchFavorite(cursorId, size);

			//then
			assertNotNull(result);
			assertEquals(2, result.size());
			verify(favoriteRepository).findFavoritesByMemberIdAndCursorId(currentMemberId, cursorId, pageable);
		}
	}

	@Test
	@DisplayName("즐겨찾기 삭제 성공")
	void removeFavorite_Success() {
		// given
		FavoriteSpot fav1 = mock(FavoriteSpot.class);
		when(fav1.getMemberId()).thenReturn(currentMemberId);

		try (MockedStatic<CurrentUserUtil> mockedStatic = mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentMemberId);

			given(favoriteRepository.findById(favorite1Id))
				.willReturn(Optional.of(fav1));

			// when
			service.removeFavorite(favorite1Id);

			// then
			verify(favoriteRepository).deleteFavoriteSpotById(favorite1Id);
		}
	}

	@Test
	@DisplayName("즐겨찾기 알림 업데이트 성공")
	void updateNotification_Success() {
		// given
		FavoriteSpot fav1 = mock(FavoriteSpot.class);
		when(fav1.getMemberId()).thenReturn(currentMemberId);

		try (MockedStatic<CurrentUserUtil> mockedStatic = mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentMemberId);

			given(favoriteRepository.findById(favorite1Id))
				.willReturn(Optional.of(fav1));

			// when
			FavoriteSpot result = service.updateNotification(favorite1Id);

			// then
			assertNotNull(result);
			assertEquals(fav1.getMemberId(), result.getMemberId());
			assertEquals(fav1.getSpotId(), result.getSpotId());
			verify(fav1).toggleNotification();
		}
	}

	@Test
	@DisplayName("즐겨찾기 알림 업데이트 실패 - 존재하지 않는 즐겨찾기")
	void updateNotification_NotFound() {
		// given

		given(favoriteRepository.findById(favorite1Id))
			.willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> service.updateNotification(favorite1Id));
		assertEquals(FavoriteErrorCode.FAVORITE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("즐겨찾기 알림 업데이트 실패 - 권한 없음")
	void updateNotification_Forbidden() {
		// given
		FavoriteSpot fav2 = mock(FavoriteSpot.class);
		when(fav2.getMemberId()).thenReturn(2L);

		try (MockedStatic<CurrentUserUtil> mockedStatic = mockStatic(CurrentUserUtil.class)) {
			mockedStatic.when(CurrentUserUtil::getCurrentUserId).thenReturn(currentMemberId);

			given(favoriteRepository.findById(favorite2Id))
				.willReturn(Optional.of(fav2));

			// when & then
			CustomException exception = assertThrows(CustomException.class,
				() -> service.updateNotification(favorite2Id));
			assertEquals(FavoriteErrorCode.FORBIDDEN_FAVORITE_ACCESS, exception.getErrorCode());
		}
	}
}