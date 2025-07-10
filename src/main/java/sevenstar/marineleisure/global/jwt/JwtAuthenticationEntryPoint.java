package sevenstar.marineleisure.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인증 예외 처리
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException)
		throws IOException, ServletException {

		log.error("Unauthorized error: {}", authException.getMessage());

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
		errorDetails.put("error", "Unauthorized");
		errorDetails.put("message", "인증이 필요합니다. 로그인 후 이용해주세요.");
		errorDetails.put("path", request.getRequestURI());

		objectMapper.writeValue(response.getOutputStream(), errorDetails);
	}
}