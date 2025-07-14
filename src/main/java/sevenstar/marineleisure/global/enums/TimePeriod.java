package sevenstar.marineleisure.global.enums;

import lombok.Getter;

@Getter
public enum TimePeriod {
	AM("오전"),
	PM("오후");
	private String description;

	TimePeriod(String description) {
		this.description = description;
	}

	public static TimePeriod from(String value) {
		for (TimePeriod timePeriod : TimePeriod.values()) {
			if (timePeriod.getDescription().equals(value)) {
				return timePeriod;
			}
		}
		throw new IllegalArgumentException("Invalid TimePeriod value: " + value);
	}

}
