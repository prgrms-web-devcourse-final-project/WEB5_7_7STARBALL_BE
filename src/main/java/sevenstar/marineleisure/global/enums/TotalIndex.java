package sevenstar.marineleisure.global.enums;

public enum TotalIndex {
	VERY_BAD("매우나쁨"),
	BAD("나쁨"),
	NORMAL("보통"),
	GOOD("좋음"),
	VERY_GOOD("매우좋음"),
	IMPOSSIBLE("체험 불가"); // 갯벌 체험 종류

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
		throw new IllegalArgumentException("Unknown total index description: " + description);
	}
}
