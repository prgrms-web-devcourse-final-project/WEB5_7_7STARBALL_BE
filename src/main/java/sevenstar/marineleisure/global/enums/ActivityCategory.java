package sevenstar.marineleisure.global.enums;

import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;

public enum ActivityCategory {
	FISHING,
	SURFING,
	SCUBA,
	MUDFLAT;

	public static ActivityCategory parse(String category) {
		try {
			return valueOf(category);
		} catch (IllegalArgumentException e) {
			throw new CustomException(CommonErrorCode.INVALID_PARAMETER);
		}
	}
}
