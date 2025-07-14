package sevenstar.marineleisure.global.config;

import java.util.List;

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
		config.addAllowedOriginPattern("*");  // 모든 오리진 허용 (실무에선 도메인 지정 권장)
		//        config.setAllowedOrigins(List.of("https://react-app")); // react app 오리진 허용. test를 위해 주석 처리
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
