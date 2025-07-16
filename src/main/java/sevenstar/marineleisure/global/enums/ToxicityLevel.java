package sevenstar.marineleisure.global.enums;

public enum ToxicityLevel {
	NONE("무해성"),
	LOW("약독성"),
	HIGH("강독성"),
	LETHAL("맹독성");

	private final String description;

	ToxicityLevel(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
