package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

public enum FavoriteErrorCode implements ErrorCode {
	INVALID_FAVORITE_PARAMETER(6400, HttpStatus.BAD_REQUEST, "즐겨찾기 id의 형식과 범위가 맞지 않습니다."),
	FORBIDDEN_FAVORITE_ACCESS(6403, HttpStatus.FORBIDDEN, "해당 즐겨찾기에 접근할 권한이 없습니다."),
	FAVORITE_NOT_FOUND(6404, HttpStatus.NOT_FOUND, "즐겨찾기를 찾을 수 없습니다.");
	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	FavoriteErrorCode(int code, HttpStatus httpStatus, String message) {
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
