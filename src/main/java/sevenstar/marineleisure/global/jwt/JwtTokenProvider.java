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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
	private final RedisBlacklistedTokenRepository redisBlacklistedTokenRepository;

	@Value("${jwt.secret:defaultSecretKeyForDevelopmentEnvironmentOnly}")
	private String secretKey;

	@Value("${jwt.access-token-validity-in-seconds:300}") // 5분
	private long accessTokenValidityInSeconds;

	@Value("${jwt.refresh-token-validity-in-seconds:86400}") // 24시간
	private long refreshTokenValidityInSeconds;

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
		Date validity = new Date(now.getTime() + refreshTokenValidityInSeconds * 1000);

		String refreshToken = Jwts.builder()
			.subject(member.getId().toString())
			.claim("email", member.getEmail())
			.claim("memberId", member.getId())
			.claim("token_type", "refresh")
			.claim("jti", jti)
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();

		return refreshToken;
	}

	public boolean validateRefreshToken(String refreshToken) {
		// 1. 먼저 Redis에서 토큰이 블랙리스트에 있는지 확인 (더 빠른 인메모리 확인)
		if (redisBlacklistedTokenRepository.isBlacklisted(refreshToken)) {
			log.info("Refresh Token is blacklisted in Redis: {}", refreshToken);
			return false;
		}

		try {
			// 2. 토큰 서명 및 만료 확인
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(refreshToken);

			// 3. 토큰이 유효하면 JTI로 RDB에서 블랙리스트 확인
			String jti = getJti(refreshToken);
			if (blacklistedRefreshTokenRepository.existsByJti(jti)) {
				log.info("Refresh Token is blacklisted in RDB by JTI: {}", jti);
				return false;
			}

			return true;
		} catch (ExpiredJwtException e) {
			log.info("Refresh Token Expired : {}", e.getMessage());
			return false;
		} catch (Exception e) {
			log.error("Refresh Token Validation Error : {}", e.getMessage());
			return false;
		}
	}

	public Long getMemberId(String refreshToken) {
		try {
			Jws<Claims> jwt = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(refreshToken);
			return jwt.getPayload().get("memberId", Long.class);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token while getting memberId: {}", e.getMessage());
			throw new IllegalArgumentException("Refresh token has expired");
		} catch (Exception e) {
			log.error("Error getting memberId from refresh token: {}", e.getMessage());
			throw new IllegalArgumentException("Invalid refresh token");
		}
	}

	/**
	 * 리프레시 토큰을 블랙리스트에 추가
	 *
	 * @param refreshToken 블랙리스트에 추가할 리프레시 토큰
	 */
	public void blacklistRefreshToken(String refreshToken) {
		try {
			// 토큰 파싱을 한 번만 수행하여 예외 처리 간소화
			Claims claims;
			try {
				claims = Jwts.parser().verifyWith(key)
					.build()
					.parseSignedClaims(refreshToken)
					.getPayload();
			} catch (ExpiredJwtException e) {
				log.info("Expired refresh token, no need to blacklist: {}", e.getMessage());
				return; // 이미 만료된 토큰은 블랙리스트에 추가할 필요 없음
			} catch (Exception e) {
				log.error("Invalid refresh token, cannot blacklist: {}", e.getMessage());
				return; // 유효하지 않은 토큰은 블랙리스트에 추가할 수 없음
			}

			String jti = claims.get("jti", String.class);
			Long memberId = claims.get("memberId", Long.class);
			Date expirationDate = claims.getExpiration();
			long expirationTime = expirationDate.getTime() - System.currentTimeMillis();

			// Redis에 토큰 블랙리스트 추가
			if (expirationTime > 0) {
				redisBlacklistedTokenRepository.addToBlacklist(refreshToken, expirationTime);
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

	public String getJti(String refreshToken) {
		try {
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(refreshToken)
				.getPayload()
				.get("jti", String.class);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token while getting JTI: {}", e.getMessage());
			throw new IllegalArgumentException("Refresh token has expired");
		} catch (Exception e) {
			log.error("Error getting JTI from refresh token: {}", e.getMessage());
			throw new IllegalArgumentException("Invalid refresh token");
		}
	}

	/**
	 * JWT 토큰 유효성 검증
	 * 토큰이 만료되었거나 서명이 유효하지 않은 경우 false를 반환합니다.
	 * 액세스 토큰은 블랙리스트 확인을 하지 않습니다.
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.info("Token Expired : {}", e.getMessage());
			return false;
		} catch (Exception e) {
			log.error("Token Validation Error : {}", e.getMessage());
			return false;
		}
	}

	/**
	 * JWT 토큰에서 인증 정보 추출
	 * 토큰에서 사용자 ID와 이메일을 추출하여 Authentication 객체를 생성합니다.
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();

		Long memberId = claims.get("memberId", Long.class);
		String email = claims.get("email", String.class);

		// 사용자 정보와 권한을 포함한 Authentication 객체 생성
		// Custom UserPrincipal 생성
		UserPrincipal principal = new UserPrincipal(memberId, email, null);

		return new UsernamePasswordAuthenticationToken(
			principal,
			null,
			null
		);
	}
}
