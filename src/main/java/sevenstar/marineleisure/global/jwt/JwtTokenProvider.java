package sevenstar.marineleisure.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sevenstar.marineleisure.member.domain.Member;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

    @Value("${jwt.secret:defaultSecretKeyForDevelopmentEnvironmentOnly}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds:300}") // 5분
    private long accessTokenValidityInSeconds;

    private SecretKey key;

    @PostConstruct
    public void init() {
//            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        // secretKey를 기반으로 Key 객체를 초기화 (최소 32byte 필요!)
        byte[] decodedKey = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public String createAccessToken(Member member) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

        return Jwts.builder()
                .subject(member.getId().toString())          // 회원 ID
                .claim("token_type", "access")
                .claim("memberId", member.getId())
                .claim("email", member.getEmail())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Member member) {
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

        String refreshToken = Jwts.builder()
                .subject(member.getId().toString())
                .claim("email", member.getEmail())
                .claim("memberId", member.getId())
                .claim("jti", jti)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();

        return refreshToken;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Refresh Token Expired : {}", e.getMessage());
            return false;
        }
    }

    public Long getMemberId(String refreshToken) {
        Jws<Claims> jwt = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken);
        return jwt.getPayload().get("memberId", Long.class);
    }

    public void blacklistRefreshToken(String refreshToken) {
        try {
            String jti = getJti(refreshToken);
            Long memberId = getMemberId(refreshToken);
            Claims claims = Jwts.parser().verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            Date expirationDate = claims.getExpiration();
            long expirationTime = expirationDate.getTime() - System.currentTimeMillis();

            // 만료를 redis에서 ?
            if (expirationTime > 0) {

            }

            LocalDateTime expiration = Instant.ofEpochMilli(expirationDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            BlacklistedRefreshToken blacklistedToken = BlacklistedRefreshToken.builder()
                    .jti(jti)
                    .memberId(memberId)
                    .expiryDate(expiration)
                    .build();

            blacklistedRefreshTokenRepository.save(blacklistedToken);
            log.info("Refresh Token Blacklisted : {}", refreshToken);
        } catch (Exception e) {
            log.error("Refresh Token Blacklist Error : {}", e.getMessage());
            throw new RuntimeException("Refresh Token Blacklist Error : " + e.getMessage());
        }
    }

    private String getJti(String refreshToken) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload()
                .get("jti", String.class);
    }
}
