
package sevenstar.marineleisure.global.api.khoa.dto.common;

import java.util.List;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
	private Response<T> response;

	@Getter
	public static class Response<T> {
		private Header header;
		private Body<T> body;
	}

	@Getter
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	public static class Body<T> {
		private Items<T> items;
		private int pageNo;
		private int numOfRows;
		private int totalCount;
		private String type;
	}

	@Getter
	public static class Items<T> {
		private List<T> item;
	}
}
