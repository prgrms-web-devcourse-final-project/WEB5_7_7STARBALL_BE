package sevenstar.marineleisure.favorite.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;

import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.dto.response.FavoritePatchDto;
import sevenstar.marineleisure.favorite.dto.vo.FavoriteItemVO;
import sevenstar.marineleisure.favorite.mapper.FavoriteMapper;
import sevenstar.marineleisure.favorite.service.FavoriteServiceImpl;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.FavoriteErrorCode;

@WebMvcTest(controllers = FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FavoriteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private FavoriteServiceImpl favoriteService;

	@MockitoBean
	private FavoriteMapper favoriteMapper;

	@Autowired
	private Validator validator;

	@Test
	@DisplayName("즐겨찾기 추가 - 성공")
	void addFavorite_Success() throws Exception {
		// given
		Long spotId = 1L;
		given(favoriteService.createFavorite(spotId)).willReturn(spotId);

		// when & then
		mockMvc.perform(post("/favorite/{id}", spotId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body").value(spotId));
	}

	@Test
	@DisplayName("즐겨찾기 목록 조회 - 성공")
	void searchFavorites_Success() throws Exception {
		// given
		Long cursorId = 0L;
		int size = 2;

		List<FavoriteItemVO> mockItems = List.of(FavoriteItemVO.builder()
			.id(1L)
			.name("장소1")
			.category(ActivityCategory.FISHING)
			.location("서울")
			.notification(true)
			.build(), FavoriteItemVO.builder()
			.id(2L)
			.name("장소2")
			.category(ActivityCategory.FISHING)
			.location("부산")
			.notification(false)
			.build(), FavoriteItemVO.builder()
			.id(3L)
			.name("장소3")
			.category(ActivityCategory.FISHING)
			.location("대구")
			.notification(true)
			.build());

		given(favoriteService.searchFavorite(cursorId, size)).willReturn(mockItems);

		// when & then
		mockMvc.perform(
				get("/favorite").param("cursorId", "0").param("size", "2").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.favorites").isArray())
			.andExpect(jsonPath("$.body.favorites.length()").value(2))
			.andExpect(jsonPath("$.body.hasNext").value(true))
			.andExpect(jsonPath("$.body.cursorId").value(0))
			.andExpect(jsonPath("$.body.size").value(2));
	}

	@Test
	@DisplayName("즐겨찾기 목록 조회 - 다음 페이지 없음")
	void searchFavorites_NoNext() throws Exception {
		// given
		Long cursorId = 0L;
		int size = 3;

		List<FavoriteItemVO> mockItems = List.of(FavoriteItemVO.builder()
			.id(1L)
			.name("장소1")
			.category(ActivityCategory.FISHING)
			.location("서울")
			.notification(true)
			.build(), FavoriteItemVO.builder()
			.id(2L)
			.name("장소2")
			.category(ActivityCategory.FISHING)
			.location("부산")
			.notification(false)
			.build());

		given(favoriteService.searchFavorite(cursorId, size)).willReturn(mockItems);

		// when & then
		mockMvc.perform(
				get("/favorite").param("cursorId", "0").param("size", "3").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.favorites").isArray())
			.andExpect(jsonPath("$.body.favorites.length()").value(2))
			.andExpect(jsonPath("$.body.hasNext").value(false));
	}

	@Test
	@DisplayName("즐겨찾기 삭제 - 성공")
	void removeFavorites_Success() throws Exception {
		// given
		Long favoriteId = 1L;
		willDoNothing().given(favoriteService).removeFavorite(favoriteId);

		// when & then
		mockMvc.perform(delete("/favorite/{id}", favoriteId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		then(favoriteService).should().removeFavorite(favoriteId);
	}

	@Test
	@DisplayName("즐겨찾기 삭제 - 잘못된 ID")
	void removeFavorites_InvalidId_Id() throws Exception {
		// given
		Long invalidId = -1L;
		willThrow(new CustomException(FavoriteErrorCode.INVALID_FAVORITE_PARAMETER)).given(favoriteService)
			.removeFavorite(invalidId);

		// when & then
		mockMvc.perform(delete("/favorite/{id}", invalidId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(FavoriteErrorCode.INVALID_FAVORITE_PARAMETER.getCode()))
			.andExpect(jsonPath("$.message").value(FavoriteErrorCode.INVALID_FAVORITE_PARAMETER.getMessage()));
	}

	@Test
	@DisplayName("즐겨찾기 알림 업데이트 - 성공")
	void updateFavorites_Success() throws Exception {
		// given
		Long favoriteId = 1L;
		FavoriteSpot mockFavoriteSpot = FavoriteSpot.builder().memberId(1L).spotId(2L).build();
		FavoritePatchDto mockDto = FavoritePatchDto.builder().favoriteId(favoriteId).notification(true).build();

		given(favoriteService.updateNotification(favoriteId)).willReturn(mockFavoriteSpot);
		given(favoriteMapper.toPatchDto(mockFavoriteSpot)).willReturn(mockDto);

		// when & then
		mockMvc.perform(patch("/favorite/{id}", favoriteId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.body.favoriteId").value(favoriteId))
			.andExpect(jsonPath("$.body.notification").value(true));
	}
}