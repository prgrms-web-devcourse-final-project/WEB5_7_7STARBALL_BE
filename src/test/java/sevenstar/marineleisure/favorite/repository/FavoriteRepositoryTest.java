package sevenstar.marineleisure.favorite.repository;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.dto.vo.FavoriteItemVO;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@DataJpaTest
class FavoriteRepositoryTest {

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private OutdoorSpotRepository outdoorSpotRepository;

	@Test
	@DisplayName("즐겨찾기 저장 및 조회 테스트")
	void saveAndFindFavorites() {
		// given
		OutdoorSpot spot1 = OutdoorSpot.builder()
			.category(ActivityCategory.FISHING)
			.type(FishingType.BOAT)
			.name("부산선상낚시")
			.location("부산 해운대")
			.latitude(BigDecimal.TEN)
			.longitude(BigDecimal.TEN)
			.build();
		OutdoorSpot spot2 = OutdoorSpot.builder()
			.category(ActivityCategory.FISHING)
			.type(FishingType.ROCK)
			.name("부산갯바위낚시")
			.location("부산 상산")
			.latitude(BigDecimal.TEN)
			.longitude(BigDecimal.TEN)
			.build();
		outdoorSpotRepository.save(spot1);
		outdoorSpotRepository.save(spot2);

		FavoriteSpot fav1 = FavoriteSpot.builder()
			.memberId(1L)
			.spotId(spot1.getId())
			.build();
		FavoriteSpot fav2 = FavoriteSpot.builder()
			.memberId(1L)
			.spotId(spot2.getId())
			.build();
		favoriteRepository.save(fav1);
		favoriteRepository.save(fav2);

		// when
		Pageable pageable = PageRequest.of(0, 2);
		List<FavoriteItemVO> results = favoriteRepository.findFavoritesByMemberIdAndCursorId(1L, null, pageable);

		//then
		Assertions.assertEquals(results.size(), 2);

		FavoriteItemVO first = results.get(0);
		Assertions.assertEquals(first.name(), "부산선상낚시");
		Assertions.assertEquals(first.category(), ActivityCategory.FISHING);
		Assertions.assertEquals(first.location(), "부산 해운대");
		Assertions.assertTrue(first.notification());

		FavoriteItemVO second = results.get(1);
		Assertions.assertEquals(second.name(), "부산갯바위낚시");
		Assertions.assertEquals(second.category(), ActivityCategory.FISHING);
		Assertions.assertEquals(second.location(), "부산 상산");
		Assertions.assertTrue(second.notification());
	}
}