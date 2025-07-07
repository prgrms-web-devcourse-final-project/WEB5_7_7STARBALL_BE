package sevenstar.marineleisure.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	/**
	 * 예외처리용
	 */

	/**
	 *	1*** : 멤버
	 */

	/**
	 * 2*** : 모임
	 */

	/**
	 * 3*** : spot
	 */

	/**
	 * 4*** : forecast
	 */

	/**
	 * 5*** : alert
	 */
	ALERT_NOT_FOUND(5404, HttpStatus.NOT_FOUND, "경보 관련된 데이터가 없습니다."),
	/**
	 * 6*** : favorite
	 */

	/**
	 * 7*** : observatory
	 */

	/**
	 * 9*** : common
	 */
	INTERNET_SERVER_ERROR(9500, HttpStatus.INTERNAL_SERVER_ERROR, "알수없는 서버 에러입니다.");
	private final int code;
	private final HttpStatus httpStatus;
	private final String message;
}
