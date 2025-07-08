package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

/**
 * 5XXX
 */

public enum AlertErrorCode implements ErrorCode {
	ALERT_NOT_FOUND(5404, HttpStatus.NOT_FOUND, "경보를 찾을수 없습니다.");

	AlertErrorCode(int code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public int getCode() {
		return 0;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return null;
	}

	@Override
	public String getMessage() {
		return "";
	}
}
