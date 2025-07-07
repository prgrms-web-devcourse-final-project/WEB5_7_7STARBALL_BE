package sevenstar.marineleisure.global.api.khoa.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FishingApiResponse {

	private Response response;

	@Getter
	@Setter
	public static class Response {
		private Header header;
		private Body body;
	}

	@Getter
	@Setter
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	@Setter
	public static class Body {
		private Items items;
		private int pageNo;
		private int numOfRows;
		private int totalCount;
		private String type;
	}

	@Getter
	@Setter
	public static class Items {
		private List<Item> item;
	}

	@Getter
	@Setter
	public static class Item {
		private String seafsPstnNm;
		private double lat;
		private double lot;
		private String predcYmd;
		private String predcNoonSeCd;
		private String seafsTgfshNm;
		private double tdlvHrScr;
		private double minWvhgt;
		private double maxWvhgt;
		private double minWtem;
		private double maxWtem;
		private double minArtmp;
		private double maxArtmp;
		private double minCrsp;
		private double maxCrsp;
		private double minWspd;
		private double maxWspd;
		private String totalIndex;
		private double lastScr;
	}
}
