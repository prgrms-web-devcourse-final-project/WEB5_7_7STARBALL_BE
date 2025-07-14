package sevenstar.marineleisure.global.utils;

import lombok.experimental.UtilityClass;
import sevenstar.marineleisure.global.enums.TotalIndex;

@UtilityClass
public class ScoreUtils {
	/**
	 * 음수일 경우 체험 불가로 제외 (가중치 계산 로직)
	 * @param index 지수
	 * @param distanceKm 거리
	 * @return 가중치
	 * indexScore * 2 + (1 / (distanceKm + 1)) * 1
	 */
	public static double calculateScore(TotalIndex index, double distanceKm) {
		int indexScore = switch(index) {
			case VERY_BAD -> 1;
			case BAD -> 2;
			case NORMAL -> 3;
			case GOOD -> 4;
			case VERY_GOOD -> 5;
			case NONE -> -1;
		};

		double distanceScore = 1.0 / (distanceKm + 1); // 정규화

		// 가중치 조절
		return indexScore * 2 + distanceScore * 1;
	}

}
