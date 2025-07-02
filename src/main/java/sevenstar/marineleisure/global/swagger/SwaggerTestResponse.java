package sevenstar.marineleisure.global.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "swagger 테스트용 response", example = "testuser")
public class SwaggerTestResponse {
	@Schema(description = "사용자 이름", example = "testuser")
	private final String username;
	@Schema(description = "비밀번호", example = "1234")
	private final String password;
}
