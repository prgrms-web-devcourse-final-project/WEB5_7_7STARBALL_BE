package sevenstar.marineleisure.global.api.khoa.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class DivingApiResponse {
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
		private List<ScubaForecastItem> item;
	}

	@Getter
	public static class ScubaForecastItem {
		private String skscExpcnRgnNm;     // 체험 지역명
		private double lat;                // 위도
		private double lot;                // 경도
		private String predcYmd;           // 예보 날짜
		private String predcNoonSeCd;      // 오전/오후/일
		private String tdlvHrCn;           // 조위 정보 (소조기/대조기 등)
		private String minWvhgt;           // 최소 파고
		private String maxWvhgt;           // 최대 파고
		private String minCrsp;            // 최소 투명도
		private String maxCrsp;            // 최대 투명도
		private String minWtem;            // 최소 수온
		private String maxWtem;            // 최대 수온
		private String totalIndex;         // 체험 지수
		private double lastScr;            // 점수
	}
}
