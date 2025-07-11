package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements ErrorCode {
	// 14XX: Client errors
	SECURITY_VALIDATION_FAILED(1403, HttpStatus.FORBIDDEN, "보안 검증에 실패했습니다."),
	REFRESH_TOKEN_MISSING(1401, HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없습니다."),
	REFRESH_TOKEN_INVALID(1402, HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
	MEMBER_NOT_FOUND(1404, HttpStatus.NOT_FOUND, "찾을수 없는 회원입니다."),

	// 15XX: Service errors
	KAKAO_LOGIN_ERROR(1500, HttpStatus.INTERNAL_SERVER_ERROR, "카카오 로그인 처리 중 오류가 발생했습니다."),
	TOKEN_REFRESH_ERROR(1501, HttpStatus.INTERNAL_SERVER_ERROR, "토큰 재발급 중 오류가 발생했습니다."),
	LOGOUT_ERROR(1502, HttpStatus.INTERNAL_SERVER_ERROR, "로그아웃 중 오류가 발생했습니다."),

	// 1XXX: 기타
	FEATURE_NOT_SUPPORTED(1001, HttpStatus.NOT_IMPLEMENTED, "지원하지 않는 기능입니다.");

	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	MemberErrorCode(int code, HttpStatus httpStatus, String message) {
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
