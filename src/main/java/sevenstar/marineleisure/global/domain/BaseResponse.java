package sevenstar.marineleisure.global.domain;

import org.springframework.http.ResponseEntity;

public record BaseResponse<T>(
	int code,
	String message,
	T body
) {
	public static <T> ResponseEntity<BaseResponse<T>> success(T body) {
		return ResponseEntity.ok(new BaseResponse<>(200, "Success", body));
	}

	public static <T> ResponseEntity<BaseResponse<T>> error(int code, int detailCode, String message) {
		return ResponseEntity.status(code).body(new BaseResponse<>(detailCode, message, null));
	}
}
