package sevenstar.marineleisure.meeting.error;

import org.springframework.http.HttpStatus;
import sevenstar.marineleisure.global.exception.enums.ErrorCode;

public enum MeetingError implements ErrorCode {
	//2XXX에러
	MEETING_NOT_FOUND(2404, HttpStatus.NOT_FOUND, "Meeting Not Found"),
	MEETING_ALREADY_FULL(2409, HttpStatus.CONFLICT, "Meeting is Full"),
	MEETING_NOT_RECRUITING(2400,HttpStatus.BAD_REQUEST,"Not Recruiting"),
	MEETING_NOT_HOST(2400,HttpStatus.BAD_REQUEST,"Not Host"),
	MEETING_NOT_LEAVE_HOST(2409,HttpStatus.CONFLICT ,"Not LeaveHost" ),
	CANNOT_LEAVE_COMPLETED_MEETING(2400,HttpStatus.BAD_REQUEST,"Cannot Leave"),
	MEETING_MEMBER_NOT_FOUND(2404, HttpStatus.NOT_FOUND, "Member Not Found"),
	;


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