package sevenstar.marineleisure.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * 쿠키 관리 유틸리티
 * 쿠키 생성, 조회, 삭제 로직을 담당합니다.
 */
@Component
public class CookieUtil {

    /**
     * 리프레시 토큰 쿠키 생성
     * 
     * @param refreshToken 리프레시 토큰
     * @return 생성된 쿠키
     */
    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (14 * 24 * 60 * 60)); // 14일
        refreshTokenCookie.setAttribute("SameSite", "None");
        return refreshTokenCookie;
    }

    /**
     * 리프레시 토큰 쿠키 삭제
     * 
     * @return 삭제용 쿠키
     */
    public Cookie deleteRefreshTokenCookie() {
        Cookie refreshTokenCookie = new Cookie("refresh_token", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 쿠키 즉시 만료
        refreshTokenCookie.setAttribute("SameSite", "None");
        return refreshTokenCookie;
    }

    /**
     * 쿠키 조회
     * 
     * @param request HTTP 요청
     * @param name 쿠키 이름
     * @return 찾은 쿠키 또는 null
     */
    public Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 쿠키 추가
     * 
     * @param response HTTP 응답
     * @param cookie 추가할 쿠키
     */
    public void addCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }
}