package sevenstar.marineleisure.global.util;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 쿠키 관리 유틸리티
 * 쿠키 생성, 조회, 삭제 로직을 담당합니다.
 * jwt.use-cookie 설정에 따라 쿠키 설정을 다르게 적용합니다.
 */
@Component
public class CookieUtil {

    @Value("${jwt.use-cookie:true}")
    private boolean useCookie;

    /**
     * 리프레시 토큰 쿠키 생성
     * jwt.use-cookie 설정에 따라 쿠키 설정이 달라집니다.
     * - useCookie=false: secure=false, sameSite=Lax
     * - useCookie=true: secure=true, sameSite=None
     *
     * @param refreshToken 리프레시 토큰
     * @return 생성된 쿠키
     */
    public Cookie createRefreshTokenCookie(String refreshToken) {
        boolean useSecureCookie = useCookie;

        // Create a standard Cookie
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(useSecureCookie);
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofDays(14).toSeconds());

        // Set SameSite attribute
        if (useSecureCookie) {
            cookie.setAttribute("SameSite", "None");
        } else {
            cookie.setAttribute("SameSite", "Lax");
        }

        return cookie;
    }

    /**
     * 리프레시 토큰 쿠키 삭제
     * jwt.use-cookie 설정에 따라 쿠키 설정이 달라집니다.
     *
     * @return 삭제용 쿠키
     */
    public Cookie deleteRefreshTokenCookie() {
        boolean useSecureCookie = useCookie;

        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(useSecureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 즉시 만료

        // Set SameSite attribute
        if (useSecureCookie) {
            cookie.setAttribute("SameSite", "None");
        } else {
            cookie.setAttribute("SameSite", "Lax");
        }

        return cookie;
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

    /**
     * 현재 설정이 쿠키를 사용하는지 여부 반환
     * 
     * @return jwt.use-cookie 설정값
     */
    public boolean isUsingCookie() {
        return useCookie;
    }
}
