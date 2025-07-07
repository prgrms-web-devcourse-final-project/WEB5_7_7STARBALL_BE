package sevenstar.marineleisure.global.enums;

public enum TotalIndex {
	VERY_BAD("매우나쁨"),
	BAD("나쁨"),
	NORMAL("보통"),
	GOOD("좋음"),
	VERY_GOOD("매우좋음");

	private final String description;

	public String getDescription() {
		return description;
	}

	TotalIndex(String description) {
		this.description = description;
	}
}
