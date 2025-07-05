package sevenstar.marineleisure.global.enums;

public enum FishingType {
	ROCK("갯바위"),
	BOAT("선상");

	private final String description;

	FishingType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
