package sevenstar.marineleisure.meeting.error;

import org.springframework.http.HttpStatus;

import sevenstar.marineleisure.global.exception.enums.ErrorCode;

public enum ParticipantError implements ErrorCode {
	PARTICIPANT_NOT_FOUND(2404, HttpStatus.NOT_FOUND, "Participant not found"),
	PARTICIPANT_NOT_EXIST(2404, HttpStatus.NOT_FOUND, "Participant not exist"),
	PARTICIPANT_ERROR_COUNT(2409,HttpStatus.CONFLICT, "Participant error count"),
	ALREADY_PARTICIPATING(2409,HttpStatus.CONFLICT, "Alrealdy participating"),;

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	ParticipantError(int code , HttpStatus httpStatus, String meesage){
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = meesage;
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
