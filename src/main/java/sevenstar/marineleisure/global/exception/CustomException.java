package sevenstar.marineleisure.global.exception;

import lombok.Getter;
import sevenstar.marineleisure.global.exception.enums.ErrorCode;

@Getter
public class CustomException extends RuntimeException {
	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
