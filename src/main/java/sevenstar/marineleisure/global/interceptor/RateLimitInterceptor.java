package sevenstar.marineleisure.global.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 레이트 리밋 인터셉터
 * 요청에 대한 레이트 리밋을 적용합니다.
 */
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ProxyManager<String> bucketProxyManager;
    private final Bandwidth bandwidth;
    private final String keyPrefix;

    public RateLimitInterceptor(ProxyManager<String> bucketProxyManager, Bandwidth bandwidth, String keyPrefix) {
        this.bucketProxyManager = bucketProxyManager;
        this.bandwidth = bandwidth;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = getClientIP(request);
        String key = keyPrefix + ":" + ip;

        // 이 IP에 대한 버킷 가져오기 또는 생성
        // ProxyManager를 사용하여 Redis에서 버킷을 가져오거나 생성
        Bucket bucket = bucketProxyManager.builder().build(key, () -> {
            BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(bandwidth)
                .build();
            return configuration;
        });

        // 토큰 소비 시도
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // 요청 허용 - 레이트 리밋 헤더 추가
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            // 요청 거부 - 레이트 리밋 헤더 추가
            long waitTimeSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            response.addHeader("X-Rate-Limit-Retry-After", String.valueOf(waitTimeSeconds));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("요청 한도를 초과했습니다. " + waitTimeSeconds + "초 후에 다시 시도하세요.");
            return false;
        }
    }

    /**
     * 클라이언트 IP 주소 가져오기
     * X-Forwarded-For 헤더가 있으면 해당 값을 사용하고, 없으면 원격 주소를 사용합니다.
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
