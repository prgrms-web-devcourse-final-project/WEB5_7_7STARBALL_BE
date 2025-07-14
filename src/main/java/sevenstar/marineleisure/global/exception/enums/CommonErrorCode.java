package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
	// 9XXX: 공통
	INTERNET_SERVER_ERROR(9500, HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),
	INVALID_PARAMETER(9400, HttpStatus.BAD_REQUEST, "잘못된 파라미터 전송되었습니다.");

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
