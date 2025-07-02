package sevenstar.marineleisure.global.swagger;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "swagger 테스트용 request", example = "testuser")
public class SwaggerTestRequest {
	@Schema(description = "사용자 이름", example = "testuser")
	private final String username;
	@Schema(description = "이메일 주소", example = "test@gmail.com")
	private final String email;
	@Schema(description = "비밀번호", example = "1234")
	private final String password;
	@Schema(description = "이미지 파일", type = "string", format = "binary")
	private final MultipartFile file;
}
