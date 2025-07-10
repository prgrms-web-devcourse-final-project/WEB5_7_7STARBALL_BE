package sevenstar.marineleisure.global.swagger;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.exception.enums.CommonErrorCode;

/**
 * Swagger 사용 예제
 * @author gunwoong
 */
@RestController
@RequestMapping("/swagger")
@Tag(name = "hello swagger", description = "Swagger 테스트 API")
public class SwaggerController {

	@Operation(summary = "Swagger get test", description = "Swagger의 GET 요청 테스트 (No Parameter)")
	@ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@GetMapping("/get")
	public ResponseEntity<BaseResponse<SwaggerTestResponse>> testGet() {
		return BaseResponse.success(new SwaggerTestResponse("swagger username", "swagger password"));
	}

	@Operation(summary = "Swagger get test", description = "Swagger의 GET 요청 테스트 (One Parameter)")
	@GetMapping("/get/{username}")
	public ResponseEntity<BaseResponse<SwaggerTestResponse>> testGet(
		@Parameter(description = "사용자 ID", example = "testUsername") @PathVariable(name = "username") String username
	) {
		return BaseResponse.success(new SwaggerTestResponse(username, "swagger password"));
	}

	@Operation(summary = "Swagger post test", description = "Swagger의 POST 요청 테스트 (request body)")
	@PostMapping("/post")
	public ResponseEntity<BaseResponse<SwaggerTestResponse>> testPost(
		@RequestBody SwaggerTestRequest swaggerTestRequest
	) {
		return BaseResponse.success(
			new SwaggerTestResponse(swaggerTestRequest.getUsername(), swaggerTestRequest.getPassword()));
	}

	@Operation(summary = "Swagger post test", description = "Swagger의 POST 요청 테스트 (model attribute)")
	@PostMapping(value = "/post", consumes = "multipart/form-data")
	public ResponseEntity<BaseResponse<SwaggerTestResponse>> uploadProfile(
		@ModelAttribute SwaggerTestRequest swaggerTestRequest
	) {
		return BaseResponse.success(
			new SwaggerTestResponse(swaggerTestRequest.getUsername(), swaggerTestRequest.getPassword()));
	}

	@Operation(summary = "사용자 삭제")
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseResponse<SwaggerTestResponse>> deleteUser(
		@Parameter(description = "삭제할 사용자 ID", example = "1") @PathVariable Long id
	) {
		return BaseResponse.error(CommonErrorCode.INTERNET_SERVER_ERROR);
	}

}
