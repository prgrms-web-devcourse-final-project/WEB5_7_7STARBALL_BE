package sevenstar.marineleisure.global.api.khoa.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class MudflatApiResponse {
	private Response response;

	@Getter
	public static class Response {
		private Header header;
		private Body body;
	}

	@Getter
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	public static class Body {
		private Items items;
		private int pageNo;
		private int numOfRows;
		private int totalCount;
		private String type;
	}

	@Getter
	public static class Items {
		private List<MarineExperienceItem> item;
	}

	@Getter
	public static class MarineExperienceItem {
		private String mdftExpcnVlgNm;       // 마을 이름
		private double lat;                  // 위도
		private double lot;                  // 경도
		private String predcYmd;             // 예측 날짜
		private String mdftExprnBgngTm;      // 체험 시작 시간
		private String mdftExprnEndTm;       // 체험 종료 시간
		private String minArtmp;             // 최소 기온
		private String maxArtmp;             // 최대 기온
		private String minWspd;              // 최소 풍속
		private String maxWspd;              // 최대 풍속
		private String weather;              // 날씨
		private String totalIndex;           // 체험지수 등급
		private double lastScr;              // 점수
	}
}
