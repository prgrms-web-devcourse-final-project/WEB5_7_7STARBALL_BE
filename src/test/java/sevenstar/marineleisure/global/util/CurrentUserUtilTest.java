package sevenstar.marineleisure.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import sevenstar.marineleisure.global.jwt.UserPrincipal;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserUtilTest {

    @Test
    @DisplayName("인증된 사용자의 ID를 가져올 수 있다")
    void getCurrentUserId() {
        // given
        Long userId = 1L;
        UserPrincipal principal = new UserPrincipal(userId, "test@example.com", null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // when & then
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            Long currentUserId = CurrentUserUtil.getCurrentUserId();
            
            assertThat(currentUserId).isEqualTo(userId);
        }
    }

    @Test
    @DisplayName("인증되지 않은 경우 getCurrentUserId 호출 시 예외가 발생한다")
    void getCurrentUserId_notAuthenticated() {
        // given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        // when & then
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            assertThatThrownBy(() -> CurrentUserUtil.getCurrentUserId())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("인증된 사용자가 아닙니다");
        }
    }

    @Test
    @DisplayName("인증된 사용자의 이메일을 가져올 수 있다")
    void getCurrentUserEmail() {
        // given
        String email = "test@example.com";
        UserPrincipal principal = new UserPrincipal(1L, email, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // when & then
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            String currentUserEmail = CurrentUserUtil.getCurrentUserEmail();
            
            assertThat(currentUserEmail).isEqualTo(email);
        }
    }

    @Test
    @DisplayName("인증되지 않은 경우 getCurrentUserEmail 호출 시 예외가 발생한다")
    void getCurrentUserEmail_notAuthenticated() {
        // given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        // when & then
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            assertThatThrownBy(() -> CurrentUserUtil.getCurrentUserEmail())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("인증된 사용자가 아닙니다");
        }
    }

    @Test
    @DisplayName("사용자가 인증되었는지 확인할 수 있다 — 인증된 경우")
    void isAuthenticated_true() {
        // given: SecurityContextHolder 에 실체 Authentication 설정
        UserPrincipal principal = new UserPrincipal(1L, "test@example.com", null);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
        SecurityContextHolder.setContext(new SecurityContextImpl(auth));

        // when
        boolean authenticated = CurrentUserUtil.isAuthenticated();

        // then
        assertThat(authenticated).isTrue();
    }

    @Test
    @DisplayName("사용자가 인증되었는지 확인할 수 있다 — 인증되지 않은 경우")
    void isAuthenticated_false() {
        // given: 컨텍스트를 비워서 anonymous 로 만듦
        SecurityContextHolder.clearContext();

        // when
        boolean authenticated = CurrentUserUtil.isAuthenticated();

        // then
        assertThat(authenticated).isFalse();
    }

    @Test
    @DisplayName("인증되지 않은 경우 isAuthenticated는 false를 반환한다")
    void isAuthenticated_notAuthenticated() {
        // given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        // when & then
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            boolean authenticated = CurrentUserUtil.isAuthenticated();
            
            assertThat(authenticated).isFalse();
        }
    }

    @Test
    @DisplayName("인증 객체가 있지만 인증되지 않은 경우 isAuthenticated는 false를 반환한다")
    void isAuthenticated_authenticationNotAuthenticated() {
        // given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // when & then
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            boolean authenticated = CurrentUserUtil.isAuthenticated();
            
            assertThat(authenticated).isFalse();
        }
    }

    @Test
    @DisplayName("인증 객체가 있지만 Principal이 UserPrincipal이 아닌 경우 isAuthenticated는 false를 반환한다")
    void isAuthenticated_principalNotUserPrincipal() {
        // given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("not a UserPrincipal");

        // when & then
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            boolean authenticated = CurrentUserUtil.isAuthenticated();
            
            assertThat(authenticated).isFalse();
        }
    }
}