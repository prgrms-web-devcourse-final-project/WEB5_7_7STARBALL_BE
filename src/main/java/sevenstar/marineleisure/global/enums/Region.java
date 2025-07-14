package sevenstar.marineleisure.global.enums;

public enum Region {
	SEOUL("서울특별시", 37.5665, 126.9780),
	BUSAN("부산광역시", 35.1796, 129.0756),
	DAEGU("대구광역시", 35.8714, 128.6014),
	INCHEON("인천광역시", 37.4563, 126.7052),
	GWANGJU("광주광역시", 35.1595, 126.8526),
	DAEJEON("대전광역시", 36.3504, 127.3845),
	ULSAN("울산광역시", 35.5384, 129.3114),
	SEJONG("세종특별자치시", 36.4801, 127.2890),
	GYEONGGI("경기도", 37.4138, 127.5183),
	GANGWON("강원특별자치도", 37.8228, 128.1555),
	CHUNGBUK("충청북도", 36.6358, 127.4913),
	CHUNGNAM("충청남도", 36.5184, 126.8000),
	JEONBUK("전라북도", 35.7167, 127.1442),
	JEONNAM("전라남도", 34.8161, 126.4630),
	GYEONGBUK("경상북도", 36.5760, 128.5056),
	GYEONGNAM("경상남도", 35.4606, 128.2132),
	JEJU("제주특별자치도", 33.4996, 126.5312);

	private final String label;
	private final double latitude;
	private final double longitude;

	Region(String label, double latitude, double longitude) {
		this.label = label;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getLabel() {
		return label;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
