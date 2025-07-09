package sevenstar.marineleisure.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 토큰 블랙리스트 저장소
 * 만료된 토큰을 Redis에 저장하여 관리합니다.
 */
@Repository
@RequiredArgsConstructor
public class RedisBlacklistedTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "blacklisted:token:";

    /**
     * 토큰을 블랙리스트에 추가
     * 
     * @param token 블랙리스트에 추가할 토큰
     * @param expirationTimeMillis 토큰 만료 시간(밀리초)
     */
    public void addToBlacklist(String token, long expirationTimeMillis) {
        String key = KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted");
        
        // 토큰 만료 시간만큼만 Redis에 저장
        if (expirationTimeMillis > 0) {
            redisTemplate.expire(key, expirationTimeMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * 
     * @param token 확인할 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    public boolean isBlacklisted(String token) {
        String key = KEY_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
}