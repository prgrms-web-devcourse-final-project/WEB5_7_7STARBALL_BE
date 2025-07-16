package sevenstar.marineleisure.global.enums;

public enum DensityLevel {
	LOW("저밀도"),
	HIGH("고밀도");

	private final String description;

	DensityLevel(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}