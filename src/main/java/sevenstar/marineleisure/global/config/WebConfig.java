package sevenstar.marineleisure.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import sevenstar.marineleisure.global.interceptor.RateLimitInterceptorFactory;

/**
 * 웹 설정
 * 인터셉터 등록 및 웹 관련 설정을 담당합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptorFactory rateLimitInterceptorFactory;

    public WebConfig(RateLimitInterceptorFactory rateLimitInterceptorFactory) {
        this.rateLimitInterceptorFactory = rateLimitInterceptorFactory;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 인증 엔드포인트에 인증 레이트 리미터 적용
        registry.addInterceptor(rateLimitInterceptorFactory.authRateLimiter())
                .addPathPatterns("/auth/**");

        // 회원 엔드포인트에 IP 기반 레이트 리미터 적용
        registry.addInterceptor(rateLimitInterceptorFactory.ipRateLimiter())
                .addPathPatterns("/members/**");

        // 활동 엔드포인트에 IP 기반 레이트 리미터 적용
        registry.addInterceptor(rateLimitInterceptorFactory.ipRateLimiter())
                .addPathPatterns("/activities/**");

        // 마지막 수단으로 모든 엔드포인트에 전역 레이트 리미터 적용
        registry.addInterceptor(rateLimitInterceptorFactory.globalRateLimiter())
                .addPathPatterns("/**");
    }
}