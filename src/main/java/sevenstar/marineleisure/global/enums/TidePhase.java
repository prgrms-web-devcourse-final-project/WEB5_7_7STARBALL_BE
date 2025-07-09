package sevenstar.marineleisure.global.enums;

public enum TidePhase {
	SPRING_TIDE("대조기"),
	Intermediate_Tide("중조기"),
	NEAP_TIDE("소조기");

	private String description;

	TidePhase(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static TidePhase parse(String origin) {
		for (TidePhase value : values()) {
			if (value.getDescription().equals(origin)) {
				return value;
			}
		}
		// TODO : exception handling
		throw new  IllegalArgumentException(
			"Invalid TidePhase description: " + origin);

	}

	public static TidePhase parse(int tideIndex) {
		if (tideIndex >= 70) {
			return SPRING_TIDE;
		}
		if (tideIndex >= 30) {
			return Intermediate_Tide;
		}
		return NEAP_TIDE;
	}

}
