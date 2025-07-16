package sevenstar.marineleisure.global.enums;

public enum TotalIndex {
	VERY_BAD("매우나쁨"),
	BAD("나쁨"),
	NORMAL("보통"),
	GOOD("좋음"),
	VERY_GOOD("매우좋음"),
	NONE("불가능"); // 갯벌 체험에서는 "체험 불가" , 스쿠버 다이빙에서 "서비스기간 아님"

	private final String description;

	public String getDescription() {
		return description;
	}

	TotalIndex(String description) {
		this.description = description;
	}

	public static TotalIndex fromDescription(String description) {
		for (TotalIndex index : values()) {
			if (index.getDescription().equals(description)) {
				return index;
			}
		}
		return NONE;
	}
}
