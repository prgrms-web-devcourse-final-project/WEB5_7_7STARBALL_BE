    package sevenstar.marineleisure.global.jwt;

    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.security.Keys;
    import jakarta.annotation.PostConstruct;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;
    import sevenstar.marineleisure.member.domain.Member;

    import java.nio.charset.StandardCharsets;
    import java.security.Key;
    import java.util.Date;
    import java.util.UUID;

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public class JwtTokenProvider {

        @Value("${jwt.secret:defaultSecretKeyForDevelopmentEnvironmentOnly}")
        private String secretKey;

        @Value("${jwt.access-token-validity-in-seconds:300}") // 5분
        private long accessTokenValidityInSeconds;

        private Key key;

        @PostConstruct
        public void init() {
//            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
            // secretKey를 기반으로 Key 객체를 초기화 (최소 32byte 필요!)
            byte[] decodedKey = secretKey.getBytes(StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(decodedKey);
        }

        public String createAccessToken(Member member) {
            Claims claims = Jwts.claims()
                    .add("email", member.getEmail())
                    .build();

            Date now = new Date();
            Date validity = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

            return Jwts.builder()
                    .claims(claims)
                    .subject(member.getEmail().toString())
                    .issuedAt(now)
                    .expiration(validity)
                    .signWith(key)
                    .compact();
        }
        public String createRefreshToken(Member member) {
            String jti = UUID.randomUUID().toString();
            Date now = new Date();
            Date validity = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

            String refreshToken = Jwts.builder()
                    .subject(member.getEmail().toString())
                    .claim("jti", jti)
                    .issuedAt(now)
                    .expiration(validity)
                    .signWith(key)
                    .compact();

            return refreshToken;
        }

    }
