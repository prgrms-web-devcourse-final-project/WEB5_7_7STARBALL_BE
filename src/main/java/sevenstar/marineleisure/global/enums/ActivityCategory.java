package sevenstar.marineleisure.global.enums;

import lombok.Getter;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;

@Getter
public enum ActivityCategory {
	FISHING("낚시"),
	SURFING("서핑"),
	SCUBA("스쿠버다이빙"),
	MUDFLAT("갯벌체험");

	private String koreanName;

	ActivityCategory(String koreanName) {
		this.koreanName = koreanName;
	}

	public static ActivityCategory parse(String category) {
		try {
			return valueOf(category);
		} catch (IllegalArgumentException e) {
			throw new CustomException(CommonErrorCode.INVALID_PARAMETER);
		}
	}
}
