package sevenstar.marineleisure.meeting.error;

import org.springframework.http.HttpStatus;

import sevenstar.marineleisure.global.exception.enums.ErrorCode;

//1XXX 에러
public enum MemberError implements ErrorCode {

	MEMBER_NOT_FOUND(1404, HttpStatus.NOT_FOUND, "Member not found"),
	MEMBER_NOT_EXIST(1404, HttpStatus.NOT_FOUND, "Member not exist"),;

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	MemberError(int code, HttpStatus httpStatus, String message) {
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