package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

public enum SpotErrorCode implements ErrorCode {
	// 3XXX: spot
	SPOT_NOT_FOUND(3404, HttpStatus.NOT_FOUND, "스팟을 찾을 수 없음"),
	DUPLICATE_FAVORITE(3409, HttpStatus.CONFLICT, "이미 즐겨찾기한 스팟");

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	SpotErrorCode(int code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
