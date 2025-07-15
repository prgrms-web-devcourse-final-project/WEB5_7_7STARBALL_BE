package sevenstar.marineleisure.global.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.jwt.JwtAuthenticationEntryPoint;
import sevenstar.marineleisure.global.jwt.JwtAuthenticationFilter;
import sevenstar.marineleisure.global.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Value("${jwt.use-cookie:true}")
	private boolean useCookie;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 허용할 엔드 포인트
			.authorizeHttpRequests(auth -> auth
				// (1) 정적 리소스
				//                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				// (2) SPA 진입점(root & index.html)
				.requestMatchers(HttpMethod.GET, "/", "/index.html").permitAll()
				// (3) 인증 API + OAuth 콜백(GET, POST)
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/oauth/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/oauth/**").permitAll()
				// (5) H2 콘솔
				.requestMatchers("/h2-console/**").permitAll()
				// Map에는 인증이 필수가 아닙니다
				.requestMatchers("/map/**").permitAll()
				// 위험경보관련 API는 인증이 필요하지 않습니다.
				.requestMatchers("/alerts/**").permitAll()
				// (6) 나머지는 인증 필요
				.anyRequest().authenticated()
			)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint))
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		// 와일드카드 대신 명시적인 오리진 목록 사용
		config.setAllowedOrigins(Arrays.asList(
			"https://your-frontend-domain.com",  // 프로덕션 환경 프론트엔드 도메인
			"http://localhost:3000",             // 개발 환경 프론트엔드 도메인
			"http://localhost:5173"              // 현재 프론트엔드 개발 환경
		));

		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));

		// jwt.use-cookie 설정에 따라 credentials 설정 변경
		// useCookie=true 일 때만 allowCredentials=true (쿠키 사용)
		config.setAllowCredentials(useCookie);
		config.setMaxAge(3600L); // 프리플라이트 요청 캐싱 (1시간)

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
