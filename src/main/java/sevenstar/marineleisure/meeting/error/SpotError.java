package sevenstar.marineleisure.meeting.error;

import org.springframework.http.HttpStatus;

import sevenstar.marineleisure.global.exception.enums.ErrorCode;

//3xxx
public enum SpotError implements ErrorCode {
	SPOT_NOT_FOUND(3404, HttpStatus.NOT_FOUND, "Spot not found");

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	SpotError(int code, HttpStatus httpStatus, String message) {
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
