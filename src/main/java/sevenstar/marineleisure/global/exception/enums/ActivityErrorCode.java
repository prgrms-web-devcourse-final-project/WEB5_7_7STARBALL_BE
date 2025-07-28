package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

/**
 * 8XXX
 */
public enum ActivityErrorCode implements ErrorCode {
	// 84XX:
	INVALID_ACTIVITY(8400, HttpStatus.NOT_FOUND, "옳바르지 않은 활동입니다."),
	WEATHER_NOT_FOUND(8404, HttpStatus.NOT_FOUND, "정보가 없습니다.");

	ActivityErrorCode(int code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
