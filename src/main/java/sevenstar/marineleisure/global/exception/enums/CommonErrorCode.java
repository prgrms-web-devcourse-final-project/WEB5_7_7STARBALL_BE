package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {

	INTERNET_SERVER_ERROR(9500, HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),
	METHOD_NOT_ALLOWED(9405, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드 입니다.");
	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	CommonErrorCode(int code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}

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
