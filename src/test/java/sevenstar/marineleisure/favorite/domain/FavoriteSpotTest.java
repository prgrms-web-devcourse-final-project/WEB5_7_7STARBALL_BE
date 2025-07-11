package sevenstar.marineleisure.favorite.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FavoriteSpotTest {
	@Test
	@DisplayName("FavoriteSpot의 notification 토글 테스트")
	void togglenotification() {
		// given

		FavoriteSpot spot = FavoriteSpot.builder()
			.spotId(1L)
			.memberId(1L)
			.build();

		// when
		spot.toggleNotification();
		// then
		Assertions.assertFalse(spot.getNotification());
	}

}