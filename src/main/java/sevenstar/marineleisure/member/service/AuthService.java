package sevenstar.marineleisure.member.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sevenstar.marineleisure.global.jwt.JwtTokenProvider;
import sevenstar.marineleisure.global.util.CookieUtil;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.LoginResponse;
import sevenstar.marineleisure.member.dto.KakaoTokenResponse;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final OauthService oauthService;
    private final CookieUtil cookieUtil;

    /**
     * 카카오 로그인 처리
     * 
     * @param code 인증 코드
     * @param response HTTP 응답
     * @return 로그인 응답 DTO
     */
    public LoginResponse processKakaoLogin(String code, HttpServletResponse response) {
        // 1. 인증 코드로 카카오 토큰 교환
        KakaoTokenResponse tokenResponse = oauthService.exchangeCodeForToken(code);
        
        // 2. 카카오 토큰으로 사용자 정보 요청 및 처리
        String accessToken = tokenResponse != null ? tokenResponse.accessToken() : null;
        if (accessToken == null) {
            log.error("Failed to get access token from Kakao");
            throw new RuntimeException("Failed to get access token from Kakao");
        }
        
        // 3. 사용자 정보 처리 및 회원 조회
        var userInfo = oauthService.processKakaoUser(accessToken);
        Member member = oauthService.findUserById((Long) userInfo.get("id"));
        
        // 4. JWT 토큰 생성
        String jwtAccessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);
        
        // 5. 리프레시 토큰 쿠키 설정
        cookieUtil.addCookie(response, cookieUtil.createRefreshTokenCookie(refreshToken));
        
        // 6. 로그인 응답 생성
        return createLoginResponse(member, jwtAccessToken);
    }
    
    /**
     * 토큰 재발급
     * 
     * @param refreshToken 리프레시 토큰
     * @param response HTTP 응답
     * @return 로그인 응답 DTO
     */
    public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) {
        // 1. 리프레시 토큰 검증
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.error("Empty refresh token");
            throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
        }
        
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.info("Invalid refresh token: {}", refreshToken);
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        
        // 2. 토큰에서 사용자 ID 추출 및 회원 조회
        Long memberId = jwtTokenProvider.getMemberId(refreshToken);
        log.info("Refreshing token for userId: {}", memberId);
        Member member = oauthService.findUserById(memberId);
        
        // 3. 기존 리프레시 토큰 블랙리스트에 추가
        jwtTokenProvider.blacklistRefreshToken(refreshToken);
        
        // 4. 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        
        // 5. 새 리프레시 토큰 쿠키 설정
        cookieUtil.addCookie(response, cookieUtil.createRefreshTokenCookie(newRefreshToken));
        
        // 6. 로그인 응답 생성
        log.info("토큰 재발급 성공: userId={}", memberId);
        return createLoginResponse(member, newAccessToken);
    }
    
    /**
     * 로그아웃
     * 
     * @param refreshToken 리프레시 토큰
     * @param response HTTP 응답
     */
    public void logout(String refreshToken, HttpServletResponse response) {
        log.info("Logging out with refresh token: {}", refreshToken);
        
        // 1. 리프레시 토큰이 있다면 블랙리스트에 추가
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                jwtTokenProvider.blacklistRefreshToken(refreshToken);
                log.info("리프레시 토큰 블랙리스트 추가 성공");
            } catch (Exception e) {
                log.error("리프레시 토큰 블랙리스트 추가 실패: {}", e.getMessage());
            }
        }
        
        // 2. 리프레시 토큰 쿠키 삭제
        cookieUtil.addCookie(response, cookieUtil.deleteRefreshTokenCookie());
        
        log.info("로그아웃 성공");
    }
    
    /**
     * 로그인 응답 DTO 생성
     * 
     * @param member 회원 정보
     * @param accessToken 액세스 토큰
     * @return 로그인 응답 DTO
     */
    private LoginResponse createLoginResponse(Member member, String accessToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .email(member.getEmail())
                .userId(member.getId())
                .nickname(member.getNickname())
                .build();
    }
}