package sevenstar.marineleisure.global.exception.enums;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	int getCode();

	HttpStatus getHttpStatus();

	String getMessage();
}
