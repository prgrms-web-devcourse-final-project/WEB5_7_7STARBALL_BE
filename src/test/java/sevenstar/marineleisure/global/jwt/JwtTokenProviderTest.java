package sevenstar.marineleisure.global.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import sevenstar.marineleisure.member.domain.Member;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

	@Mock
	private BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

	@Mock
	private RedisBlacklistedTokenRepository redisBlacklistedTokenRepository;

	@InjectMocks
	private JwtTokenProvider jwtTokenProvider;

	private Member testMember;
	private String secretKey = "testSecretKeyWithAtLeast32Characters1234567890";

	@BeforeEach
	void setUp() {
		// 필요한 프로퍼티 설정
		ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", secretKey);
		ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInSeconds", 300L); // 5분

		// init 메서드 호출
		jwtTokenProvider.init();

		// 테스트용 Member 객체 생성
		testMember = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.latitude(BigDecimal.valueOf(37.5665))
			.longitude(BigDecimal.valueOf(126.9780))
			.build();

		// ID 설정 (리플렉션 사용)
		ReflectionTestUtils.setField(testMember, "id", 1L);
	}

	@Test
	@DisplayName("액세스 토큰을 생성할 수 있다")
	void createAccessToken() {
		// when
		String accessToken = jwtTokenProvider.createAccessToken(testMember);

		// then
		assertThat(accessToken).isNotNull();

		// 토큰 검증
		Claims claims = Jwts.parser()
			.verifyWith((SecretKey)ReflectionTestUtils.getField(jwtTokenProvider, "key"))
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();

		assertThat(claims.getSubject()).isEqualTo("1");
		assertThat(claims.get("token_type")).isEqualTo("access");
		assertThat(claims.get("memberId")).isEqualTo(1);
		assertThat(claims.get("email")).isEqualTo("test@example.com");

		// 만료 시간 검증 (현재 시간 + 5분 이내)
		long expirationTime = claims.getExpiration().getTime();
		long currentTime = System.currentTimeMillis();
		long fiveMinutesInMillis = 5 * 60 * 1000;

		assertThat(expirationTime).isGreaterThan(currentTime);
		assertThat(expirationTime).isLessThanOrEqualTo(currentTime + fiveMinutesInMillis);
	}

	@Test
	@DisplayName("리프레시 토큰을 생성할 수 있다")
	void createRefreshToken() {
		// when
		String refreshToken = jwtTokenProvider.createRefreshToken(testMember);

		// then
		assertThat(refreshToken).isNotNull();

		// 토큰 검증
		Claims claims = Jwts.parser()
			.verifyWith((SecretKey)ReflectionTestUtils.getField(jwtTokenProvider, "key"))
			.build()
			.parseSignedClaims(refreshToken)
			.getPayload();

		assertThat(claims.getSubject()).isEqualTo("1");
		assertThat(claims.get("token_type")).isEqualTo("refresh");
		assertThat(claims.get("memberId")).isEqualTo(1);
		assertThat(claims.get("email")).isEqualTo("test@example.com");
		assertThat(claims.get("jti")).isNotNull(); // JTI 존재 확인

		// 만료 시간 검증 (현재 시간 + 5분 이내)
		long expirationTime = claims.getExpiration().getTime();
		long currentTime = System.currentTimeMillis();
		long fiveMinutesInMillis = 5 * 60 * 1000;

		assertThat(expirationTime).isGreaterThan(currentTime);
		assertThat(expirationTime).isLessThanOrEqualTo(currentTime + fiveMinutesInMillis);
	}

	@Test
	@DisplayName("유효한 토큰을 검증할 수 있다")
	void validateToken_validToken() {
		// given
		String token = jwtTokenProvider.createAccessToken(testMember);

		// when
		boolean isValid = jwtTokenProvider.validateToken(token);

		// then
		assertThat(isValid).isTrue();
	}

	@Test
	@DisplayName("만료된 토큰은 유효하지 않다")
	void validateToken_expiredToken() {
		// given
		// 만료된 토큰 생성 (현재 시간 - 1시간)
		Date now = new Date();
		Date expiration = new Date(now.getTime() - 3600000); // 1시간 전

		SecretKey key = (SecretKey)ReflectionTestUtils.getField(jwtTokenProvider, "key");
		String expiredToken = Jwts.builder()
			.subject(testMember.getId().toString())
			.claim("token_type", "access")
			.claim("memberId", testMember.getId())
			.claim("email", testMember.getEmail())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(key)
			.compact();

		// when
		boolean isValid = jwtTokenProvider.validateToken(expiredToken);

		// then
		assertThat(isValid).isFalse();
	}

	@Test
	@DisplayName("유효한 리프레시 토큰을 검증할 수 있다")
	void validateRefreshToken_validToken() {
		// given
		String refreshToken = jwtTokenProvider.createRefreshToken(testMember);

		// Redis와 RDB에서 블랙리스트 확인 결과 설정
		when(redisBlacklistedTokenRepository.isBlacklisted(refreshToken)).thenReturn(false);
		when(blacklistedRefreshTokenRepository.existsByJti(anyString())).thenReturn(false);

		// when
		boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);

		// then
		assertThat(isValid).isTrue();

		// 검증 메서드 호출 확인
		verify(redisBlacklistedTokenRepository).isBlacklisted(refreshToken);
		verify(blacklistedRefreshTokenRepository).existsByJti(anyString());
	}

	@Test
	@DisplayName("Redis 블랙리스트에 있는 리프레시 토큰은 유효하지 않다")
	void validateRefreshToken_blacklistedInRedis() {
		// given
		String refreshToken = jwtTokenProvider.createRefreshToken(testMember);

		// Redis 블랙리스트에 있는 것으로 설정
		when(redisBlacklistedTokenRepository.isBlacklisted(refreshToken)).thenReturn(true);

		// when
		boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);

		// then
		assertThat(isValid).isFalse();

		// Redis 확인 후 RDB는 확인하지 않아야 함
		verify(redisBlacklistedTokenRepository).isBlacklisted(refreshToken);
		verify(blacklistedRefreshTokenRepository, never()).existsByJti(anyString());
	}

	@Test
	@DisplayName("RDB 블랙리스트에 있는 리프레시 토큰은 유효하지 않다")
	void validateRefreshToken_blacklistedInRDB() {
		// given
		String refreshToken = jwtTokenProvider.createRefreshToken(testMember);

		// Redis에는 없지만 RDB에는 있는 것으로 설정
		when(redisBlacklistedTokenRepository.isBlacklisted(refreshToken)).thenReturn(false);
		when(blacklistedRefreshTokenRepository.existsByJti(anyString())).thenReturn(true);

		// when
		boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);

		// then
		assertThat(isValid).isFalse();

		// 두 저장소 모두 확인해야 함
		verify(redisBlacklistedTokenRepository).isBlacklisted(refreshToken);
		verify(blacklistedRefreshTokenRepository).existsByJti(anyString());
	}

	@Test
	@DisplayName("리프레시 토큰을 블랙리스트에 추가할 수 있다")
	void blacklistRefreshToken() {
		// given
		String refreshToken = jwtTokenProvider.createRefreshToken(testMember);
		String jti = jwtTokenProvider.getJti(refreshToken);

		// 블랙리스트 저장 설정
		when(blacklistedRefreshTokenRepository.save(any(BlacklistedRefreshToken.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		jwtTokenProvider.blacklistRefreshToken(refreshToken);

		// then
		// Redis와 RDB에 저장되었는지 확인
		verify(redisBlacklistedTokenRepository).addToBlacklist(eq(refreshToken), anyLong());
		verify(blacklistedRefreshTokenRepository).save(any(BlacklistedRefreshToken.class));
	}

	@Test
	@DisplayName("토큰에서 인증 정보를 추출할 수 있다")
	void getAuthentication() {
		// given
		String token = jwtTokenProvider.createAccessToken(testMember);

		// when
		Authentication authentication = jwtTokenProvider.getAuthentication(token);

		// then
		assertThat(authentication).isNotNull();

		// principal 이 UserPrincipal 인지 확인하고, ID·이메일 검증
		assertThat(authentication.getPrincipal()).isInstanceOf(UserPrincipal.class);
		UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
		assertThat(principal.getId()).isEqualTo(testMember.getId());
		assertThat(principal.getUsername()).isEqualTo("test@example.com");

		// credentials 는 null
		assertThat(authentication.getCredentials()).isNull();
	}

	@Test
	@DisplayName("토큰에서 회원 ID를 추출할 수 있다")
	void getMemberId() {
		// given
		String token = jwtTokenProvider.createRefreshToken(testMember);

		// when
		Long memberId = jwtTokenProvider.getMemberId(token);

		// then
		assertThat(memberId).isEqualTo(1L);
	}

	@Test
	@DisplayName("리프레시 토큰에서 JTI를 추출할 수 있다")
	void getJti() {
		// given
		String refreshToken = jwtTokenProvider.createRefreshToken(testMember);

		// when
		String jti = jwtTokenProvider.getJti(refreshToken);

		// then
		assertThat(jti).isNotNull();
		assertThat(jti).isNotEmpty();
	}
}