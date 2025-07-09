package sevenstar.marineleisure.global.enums;

import java.util.List;

public enum FishingType {
	ROCK("갯바위"),
	BOAT("선상"),
	NONE("없음");

	private final String description;

	FishingType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static List<FishingType> getFishingTypes() {
		return List.of(ROCK, BOAT);
	}
}
