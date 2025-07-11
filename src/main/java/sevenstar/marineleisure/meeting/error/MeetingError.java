package sevenstar.marineleisure.meeting.error;

import org.springframework.http.HttpStatus;
import sevenstar.marineleisure.global.exception.enums.ErrorCode;

public enum MeetingError implements ErrorCode {
	MEETING_NOT_FOUND(2404, HttpStatus.NOT_FOUND, "Meeting Not Found");

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;


	MeetingError(int code, HttpStatus httpStatus, String message) {
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