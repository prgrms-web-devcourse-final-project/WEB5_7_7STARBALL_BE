package sevenstar.marineleisure.global.interceptor;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.distributed.proxy.ProxyManager;

/**
 * 레이트 리밋 인터셉터 팩토리
 * 다양한 유형의 레이트 리미터를 생성합니다.
 */
@Component
public class RateLimitInterceptorFactory {

    private final ProxyManager<String> bucketProxyManager;
    private final Bandwidth globalBandwidth;
    private final Bandwidth ipBandwidth;
    private final Bandwidth authBandwidth;

    public RateLimitInterceptorFactory(
            ProxyManager<String> bucketProxyManager,
            Bandwidth globalBandwidth,
            Bandwidth ipBandwidth,
            Bandwidth authBandwidth) {
        this.bucketProxyManager = bucketProxyManager;
        this.globalBandwidth = globalBandwidth;
        this.ipBandwidth = ipBandwidth;
        this.authBandwidth = authBandwidth;
    }

    /**
     * 전역 레이트 리미터 생성
     * 모든 요청에 대해 초당 100개의 요청으로 제한합니다.
     */
    public RateLimitInterceptor globalRateLimiter() {
        return new RateLimitInterceptor(bucketProxyManager, globalBandwidth, "global");
    }

    /**
     * IP 기반 레이트 리미터 생성
     * IP당 초당 10개의 요청으로 제한합니다.
     */
    public RateLimitInterceptor ipRateLimiter() {
        return new RateLimitInterceptor(bucketProxyManager, ipBandwidth, "ip");
    }

    /**
     * 인증 엔드포인트용 레이트 리미터 생성
     * IP당 10초에 5개의 요청으로 제한합니다.
     */
    public RateLimitInterceptor authRateLimiter() {
        return new RateLimitInterceptor(bucketProxyManager, authBandwidth, "auth");
    }
}