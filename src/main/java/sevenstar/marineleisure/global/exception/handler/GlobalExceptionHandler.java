package sevenstar.marineleisure.global.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<BaseResponse<Void>> handleCustomException(CustomException ex) {
		return BaseResponse.error(ex.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception ex) {
		ex.printStackTrace();
		return BaseResponse.error(CommonErrorCode.INTERNET_SERVER_ERROR);
	}
}